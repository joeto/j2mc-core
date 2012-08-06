package to.joe.j2mc.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class ThreadSafePermissionTracker implements Listener, Runnable {

    private final Plugin plugin;
    private final String perm;
    private final Map<String, Boolean> havingPerm;

    public ThreadSafePermissionTracker(Plugin plugin, String permission) {
        this.plugin = plugin;
        this.perm = permission;
        this.havingPerm = new ConcurrentHashMap<String, Boolean>();

        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 10, 10);
    }

    public boolean hasPermission(Player player) {
        return this.hasPermission(player.getName());
    }

    public boolean hasPermission(String playerName) {
        return this.havingPerm.containsKey(playerName);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission(this.perm)) {
            this.havingPerm.put(event.getPlayer().getName(), true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.havingPerm.remove(event.getPlayer().getName());
    }

    @Override
    public void run() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission(this.perm)) {
                this.havingPerm.put(player.getName(), true);
            } else {
                this.havingPerm.remove(player.getName());
            }
        }
    }

}
