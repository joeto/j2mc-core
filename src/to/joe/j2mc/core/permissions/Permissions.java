package to.joe.j2mc.core.permissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import to.joe.j2mc.core.Debug;
import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;

public class Permissions implements Listener {

    private final J2MC_Core plugin;

    /*
     * Flag documentation
     * 
     * p - teleport protection
     * t - trusted
     * d - donator
     * N - NSA
     * k - kibbles
     * 
     */

    /*
     * groups: default, admin, srstaff
     */

    private HashMap<Character, HashMap<String, Boolean>> permissions;
    private HashMap<Character, HashMap<String, Boolean>> modulePermissions = new HashMap<Character, HashMap<String,Boolean>>();
    private HashMap<String, PermissionAttachment> attachments;
    private HashMap<String, HashSet<Character>> playerFlags;
    private HashMap<String, HashSet<Character>> groupFlags;
    private HashMap<String, String> playerGroup;
    private Player[] playerCache;
    public Set<String> adminCache;

    public Permissions(J2MC_Core plugin) {
        this.plugin = plugin;
        this.attachments = new HashMap<String, PermissionAttachment>();
        this.loadGroupsAndPermissions();
        this.setPlayerCache(plugin.getServer().getOnlinePlayers());
        J2MC_Manager.getCore().getServer().getScheduler().scheduleSyncRepeatingTask(J2MC_Manager.getCore(), new Runnable() {
            @Override
            public void run() {
                Permissions.this.setPlayerCache(Permissions.this.plugin.getServer().getOnlinePlayers());
                for (Player player : Permissions.this.plugin.getServer().getOnlinePlayers()) {
                    if (player.hasPermission("j2mc.core.admin")) {
                        Permissions.this.adminCache.add(player.getName());
                    } else {
                        Permissions.this.adminCache.remove(player.getName());
                    }
                }
            }
        }, 100, 100);
        J2MC_Manager.getCore().getServer().getPluginManager().registerEvents(this, J2MC_Manager.getCore());
    }
    
    /**
     * Retrieve player cache for thread safe methods
     * @return the players on the server
     */
    public synchronized Player[] getPlayerCache() {
        return this.playerCache;
    }
    /**
     * Set player cache for thread safe methods
     */
    public synchronized void setPlayerCache(Player[] list) {
        this.playerCache = list.clone();
    }
    
    /**
     * Reload groups and permissions
     */
    public void loadGroupsAndPermissions() {
        this.permissions = new HashMap<Character, HashMap<String, Boolean>>();
        this.playerFlags = new HashMap<String, HashSet<Character>>();
        this.groupFlags = new HashMap<String, HashSet<Character>>();
        this.playerGroup = new HashMap<String, String>();
        this.adminCache = Collections.synchronizedSet(new HashSet<String>());
        this.permissions.putAll(modulePermissions);
        try {
            final PreparedStatement statement = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `name`,`flags` FROM `groups` WHERE `server_id`=?");
            statement.setInt(1, J2MC_Manager.getServerID());
            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final String flagList = result.getString("flags");
                final HashSet<Character> flags = new HashSet<Character>();
                for (final char flag : flagList.toCharArray()) {
                    flags.add(flag);
                }
                final String groupname = result.getString("name");
                this.groupFlags.put(groupname, flags);
                Debug.log(groupname + " " + flags);
            }
            if (!this.groupFlags.containsKey("default")) {
                throw new Exception();
            }
            final PreparedStatement readPermissions = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `permission`, `flag`, `value` FROM `perms` WHERE `server_id`=?");
            readPermissions.setInt(1, J2MC_Manager.getServerID());
            final ResultSet readPermissionsResult = readPermissions.executeQuery();
            while (readPermissionsResult.next()) {
                final String permission = readPermissionsResult.getString("permission");
                final String flagString = readPermissionsResult.getString("flag");
                final boolean value = readPermissionsResult.getBoolean("value");
                final char flag = flagString.toCharArray()[0];
                if (!this.permissions.containsKey(flag)) {
                    this.permissions.put(flag, new HashMap<String, Boolean>());
                }
                this.permissions.get(flag).put(permission, value);
                Debug.log(flag + " " + permission + " " + value);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            plugin.buggerAll("Could not load SQL groups");
        }
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player != null) {
                this.initializePlayerPermissions(player.getName());
                this.refreshPermissions(player);
            }
        }
    }
    
    /**
     * Call to add a priority flag -> permission relation
     */
    public void addFlagPermissionRelation(String permissionNode, char flag, boolean value) {
        if (!this.modulePermissions.containsKey(flag)) {
            this.modulePermissions.put(flag, new HashMap<String, Boolean>());
        }
        this.modulePermissions.get(flag).put(permissionNode, value);
        if (!this.permissions.containsKey(flag)) {
            this.permissions.put(flag, new HashMap<String, Boolean>());
        }
        this.permissions.get(flag).put(permissionNode, value);
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player != null) {
                this.refreshPermissions(player);
            }
        }
    }
    
    /**
     * Call to remove a permission from a player
     * 
     * @param player the player whose permission should be removed
     * @param permission the permission to be removed
     */
    public void removePermission(Player player, String permission) {
        PermissionAttachment pa = this.attachments.get(player.getName());
        pa.unsetPermission(permission);
    }

    /**
     * Temporarily add a flag to a player
     * 
     * @param player the player who the flag should be added to
     * @param flag the flag
     */
    public void addFlag(Player player, char flag) {
        this.playerFlags.get(player.getName()).add(flag);
        this.refreshPermissions(player);
    }

    /**
     * Add a permanent flag to a player
     * 
     * @param player the player to add the flag to
     * @param flag the flag to add
     */
    public void addPermanentFlag(Player player, char flag) {
        final HashSet<Character> newFlags = this.playerFlags.get(player.getName());
        newFlags.add(flag);
        this.playerFlags.put(player.getName(), newFlags);
        String toAdd = "";
        for (final char derp : this.playerFlags.get(player.getName())) {
            toAdd += derp;
        }
        try {
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("UPDATE `users` SET flags=? WHERE name=?");
            ps.setString(1, toAdd);
            ps.setString(2, player.getName());
            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        this.refreshPermissions(player);
    }

    /**
     * Add a permanent flag to a player (use for offline players)
     * 
     * @param player the player to add the flag to
     * @param flag the flag to add
     */
    public void addPermanentFlag(String player, char flag) {
        try {
            final PreparedStatement grab = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `flags` FROM `users` WHERE name=?");
            grab.setString(1, player);
            final ResultSet rs = grab.executeQuery();
            rs.next();
            final String toAdd = rs.getString("flags") + flag;
            final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("UPDATE `users` SET flags=? WHERE name=?");
            ps.setString(1, toAdd);
            ps.setString(2, player);
            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a flag from a player, temporarily
     * 
     * @param player the player from whom to remove it
     * @param flag the flag to remove
     */
    public void delFlag(Player player, char flag) {
        this.playerFlags.get(player.getName()).remove(flag);
        this.refreshPermissions(player);
    }

    /**
     * Check if player has flag
     * 
     * @param player
     *            - Player to check
     * @param flag
     *            - Flag to check
     * 
     * @return Returns true if player has flag, returns false if doesn't.
     */
    public boolean hasFlag(String player, char flag) {
        return this.playerFlags.get(player) != null && this.playerFlags.get(player).contains(flag);
    }

    public void initializePlayerPermissions(String name) {
        final HashSet<Character> flags = new HashSet<Character>();
        String group;
        try {
            final PreparedStatement userInfo = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `group`,`flags` FROM `users` WHERE `name`=?");
            userInfo.setString(1, name);
            final ResultSet result = userInfo.executeQuery();
            if (result.next()) {
                group = result.getString("group");
                final String flagList = result.getString("flags");
                if (flagList != null) {
                    for (final char flag : flagList.toCharArray()) {
                        flags.add(flag);
                    }
                }
            } else {
                final PreparedStatement newPlayer = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("INSERT INTO `users` (`name`, `group`, `flags`) VALUES ( ?, ? , ?)");
                newPlayer.setString(1, name);
                newPlayer.setString(2, "default");
                newPlayer.setString(3, "");
                newPlayer.executeUpdate();
                group = "default";
            }
        } catch (final Exception e) {
            e.printStackTrace();
            group = "default";
        }

        this.playerGroup.put(name, group);
        this.playerFlags.put(name, flags);
    }
    
    /**
     * Checks if player is admin, thread safe.
     * @param playerName the player's name
     *
     */
    public boolean isAdmin(String playerName) {
        return this.adminCache.contains(playerName);
    }
    
    /**
     * Returns player's flags
     * 
     * @param player the player whose flags you want
     * 
     */
    public HashSet<Character> getFlags(String player) {
        final HashSet<Character> flags = new HashSet<Character>();
        if (this.playerFlags.get(player) != null) {
            flags.addAll(this.playerFlags.get(player));
        }
        final String group = this.playerGroup.get(player);
        if (this.groupFlags.get(group) != null) {
            flags.addAll(this.groupFlags.get(group));
        }
        return flags;
    }

    /**
     * Called when a player joins the game.
     * Do not call this
     * 
     * @param event the login event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerLogin(PlayerLoginEvent event) {
        this.initializePlayerPermissions(event.getPlayer().getName());
        this.refreshPermissions(event.getPlayer());
        if (event.getPlayer().hasPermission("j2mc.core.admin")) {
            this.adminCache.add(event.getPlayer().getName());
        }
    }

    /**
     * Called when the player quits
     * Do not call this.
     * 
     * @param event the player quit event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.attachments.remove(player.getName());
        this.playerFlags.remove(player.getName());
        this.adminCache.remove(player.getName());
    }

    /**
     * GOOD GOD MAN, call this before replacing this class or shutting down
     */
    public void shutdown() {
        for (final String playerName : this.attachments.keySet()) {
            final Player player = this.plugin.getServer().getPlayer(playerName);
            if (player != null) {
                player.removeAttachment(this.attachments.get(playerName));
            }
        }
        this.plugin.getLogger().info("Unloaded all permissions");
    }

    private void refreshPermissions(Player player) {
        final String name = player.getName();
        if (this.attachments.containsKey(name)) {
            for (PermissionAttachmentInfo PAInfo : player.getEffectivePermissions()) {
                if (PAInfo != null && PAInfo.getAttachment() != null && this.attachments.get(name) != null) {
                    if (PAInfo.getAttachment().equals(this.attachments.get(name))) {
                        player.removeAttachment(this.attachments.remove(name));
                        break;
                    }
                }
            }
        }
        final PermissionAttachment attachment = player.addAttachment(this.plugin);

        final HashSet<Character> flags = new HashSet<Character>();
        if (this.playerFlags.get(name) != null) {
            flags.addAll(this.playerFlags.get(name));
        }
        final String group = this.playerGroup.get(name);
        if (this.groupFlags.get(group) != null) {
            flags.addAll(this.groupFlags.get(group));
        }
        final HashSet<Character> completed = new HashSet<Character>();
        Debug.log("Joining: " + player.getName());
        for (final Character flag : flags) {
            Debug.log("Flag: " + flag);
            if (completed.contains(flag)) {
                continue;
            }
            completed.add(flag);
            if (this.permissions.containsKey(flag)) {
                final HashMap<String, Boolean> permissionsAndValue = this.permissions.get(flag);
                for (Map.Entry<String, Boolean> entry : permissionsAndValue.entrySet()) {
                    Debug.log("Node: " + entry.getKey() + ", Value: " + entry.getValue());
                    attachment.setPermission(entry.getKey(), entry.getValue());
                }
            }
        }
        this.attachments.put(name, attachment);
    }

}
