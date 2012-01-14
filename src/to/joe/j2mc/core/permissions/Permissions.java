package to.joe.j2mc.core.permissions;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;

public class Permissions {

    private final J2MC_Core plugin;

    /*
     * Flag documentation
     * 
     * p - teleport protection
     * t - trusted
     * 
     */

    /*
     * MySQL tables
     * users
     *  user_name
     *  group_id
     *  user_flags
     * groups
     *  group_id
     *  server_id
     *  group_name
     *  group_flags
     * permissions
     *  permission
     *  flag
     * 
     */

    private final HashMap<Character, HashSet<String>> permissions;
    private final HashSet<Character> authenticationFlags;
    private final HashMap<String, PermissionAttachment> attachments;
    private final HashMap<String, HashSet<Character>> playerFlags;
    private final HashSet<String> authenticated;

    public Permissions(J2MC_Core plugin, HashSet<Character> authenticationFlags) {
        this.plugin = plugin;
        this.authenticationFlags = authenticationFlags;
        this.permissions = new HashMap<Character, HashSet<String>>();
        this.authenticated = new HashSet<String>();
        this.attachments = new HashMap<String, PermissionAttachment>();
        this.playerFlags = new HashMap<String, HashSet<Character>>();
    }

    public void addFlag(Player player, char flag, boolean persist) {
        this.playerFlags.get(player.getName()).add(flag);
        this.refreshPermissions(player);
        if (persist) {
            //TODO store in SQL
        }
    }

    public void delFlag(Player player, char flag, boolean persist) {
        this.playerFlags.get(player.getName()).remove(flag);
        this.refreshPermissions(player);
        if (persist) {
            //TODO store in SQL
        }
    }

    public boolean isAuthenticated(Player player) {
        return this.authenticated.contains(player.getName());
    }

    public void playerJoin(Player player) {
        final HashSet<Character> flags = new HashSet<Character>();
        //TODO MySQL stuff
        this.playerFlags.put(player.getName(), flags);
        this.refreshPermissions(player);
    }

    public void playerQuit(Player player) {
        this.attachments.remove(player.getName());
        this.playerFlags.remove(player.getName());
    }

    public void setAuthenticated(Player player, boolean authenticated) {
        if (authenticated) {
            this.authenticated.add(player.getName());
        } else {
            this.authenticated.remove(player.getName());
        }
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
        J2MC_Manager.getLog().info("Unloaded all permissions");
    }

    private void refreshPermissions(Player player) {
        final String name = player.getName();
        if (this.attachments.containsKey(name)) {
            player.removeAttachment(this.attachments.remove(name));
        }
        final PermissionAttachment attachment = player.addAttachment(this.plugin);
        this.attachments.put(name, attachment);
        for (final Character flag : this.playerFlags.get(name)) {
            if (this.authenticationFlags.contains(flag) && !this.isAuthenticated(player)) {
                continue;
            }
            for (final String permission : this.permissions.get(flag)) {
                attachment.setPermission(permission, true);
            }
        }
    }

}
