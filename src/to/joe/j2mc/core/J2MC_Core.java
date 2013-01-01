package to.joe.j2mc.core;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.staticaccess.VanishNoPacket;

import to.joe.j2mc.core.MySQL.MySQL;
import to.joe.j2mc.core.command.DebugCommand;
import to.joe.j2mc.core.command.ReloadPermissionsCommand;
import to.joe.j2mc.core.log.LogColors;
import to.joe.j2mc.core.permissions.Permissions;
import to.joe.j2mc.core.visibility.Visibility;

public class J2MC_Core extends JavaPlugin {

    /**
     * Combine string array with specified separator
     *
     * @param startIndex the index from where to start in the array
     * @param string the string array to be glued together
     * @param separator the "glue" used to join the array elements
     * @return the string array elements in a string object joined by the separator
     */
    public static String combineSplit(int startIndex, String[] string, String separator) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(separator);
        }
        builder.setLength(builder.length() - separator.length());
        return builder.toString();
    }

    /**
     * Send message to those with j2mc.core.admin and to the server log as INFO
     * This method is thread safe.
     *
     * @param message the message to be logged and sent
     */
    public void adminAndLog(String message) {
        for (final Player player : J2MC_Manager.getPermissions().getPlayerCache()) {
            if (J2MC_Manager.getPermissions().isAdmin(player.getName())) {
                player.sendMessage(message);
            }
        }
        this.getLogger().info(LogColors.process(message));
    }

    /**
     * OMG SHUT THIS DOWN Do not use this from outside core.
     *
     * @param reason the reason we're dying
     */
    public void buggerAll(String reason) {
        this.buggerAll(reason, null);
    }

    /**
     * OMG SHUT THIS DOWN (with added exception print) Do not use this from
     * outside core.
     *
     * @param reason  the reason we're dying
     * @param exception the exception to print
     */
    public void buggerAll(String reason, Exception exception) {
        if (exception != null) {
            this.getLogger().log(Level.SEVERE, "Shutdown caused by: " + reason, exception);
        } else {
            this.getLogger().severe("Shutdown caused by: " + reason);
        }
        this.getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Send a message to users without the specified permission
     *
     * @param permission the permission that players without should receive the message
     * @param message the message
     */
    public void messageByNoPermission(String message, String permission) {
        for (final Player player : this.getServer().getOnlinePlayers()) {
            if ((player != null) && !player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    public void messageNonAdmin(String message) {
        this.messageByNoPermission(message, "j2mc.core.admin");
    }

    @Override
    public void onDisable() {
        J2MC_Manager.getPermissions().shutdown();
    }

    @Override
    public void onEnable() {
        J2MC_Manager.getInstance().setCore(this);
        Debug.enable(this.getConfig().getBoolean("debug", false));
        final String mySQLUsername = this.getConfig().getString("MySQL.username");
        if (mySQLUsername == null) {
            this.buggerAll("Config is empty. I repeat, config is derp");
            return;
        }
        final String mySQLPassword = this.getConfig().getString("MySQL.password");
        final String mySQLDatabase = this.getConfig().getString("MySQL.database");
        try {
            J2MC_Manager.getInstance().setMySQL(new MySQL(mySQLDatabase, mySQLUsername, mySQLPassword, this));
        } catch (Exception e) {
            J2MC_Core.this.buggerAll("SQL failure", e);
            return;
        }

        J2MC_Manager.getInstance().setServerID(this.getConfig().getInt("General.server-id"));

        J2MC_Manager.getInstance().setPermissions(new Permissions(this));
        this.getCommand("reloadpermissions").setExecutor(new ReloadPermissionsCommand(this));
        this.getCommand("debugcore").setExecutor(new DebugCommand(this));

        J2MC_Manager.getInstance().setVisibility(new Visibility());

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (!J2MC_Core.this.getServer().getPluginManager().isPluginEnabled("VanishNoPacket")) {
                    J2MC_Core.this.buggerAll("VanishNoPacket required.");
                    return;
                }
                try {
                    VanishNoPacket.numVanished();
                } catch (final Exception e) {
                    J2MC_Core.this.buggerAll("VanishNoPacket required.");
                }
            }

        });
    }

}