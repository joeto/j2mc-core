package to.joe.j2mc.core.util;

import java.util.HashSet;
import java.util.Set;

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
    private final Set<String> havingPerm;

    public ThreadSafePermissionTracker(Plugin plugin, String permission) {
        this.plugin = plugin;
        this.perm = permission;
        this.havingPerm = new HashSet<String>() {
            private static final long serialVersionUID = 1L;
            private final Object sync = new Object();

            @Override
            public boolean add(String e) {
                synchronized (this.sync) {
                    return super.add(e.toLowerCase());
                }
            }

            @Override
            public boolean contains(Object o) {
                return super.contains(((String) o).toLowerCase());
            }

            @Override
            public boolean remove(Object o) {
                synchronized (this.sync) {
                    return super.remove(((String) o).toLowerCase());
                }
            }
        };

        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 10, 10);
    }

    public boolean hasPermission(Player player) {
        return this.hasPermission(player.getName());
    }

    public boolean hasPermission(String playerName) {
        return this.havingPerm.contains(playerName);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission(this.perm)) {
            this.havingPerm.add(event.getPlayer().getName());
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
                this.havingPerm.add(player.getName());
            } else {
                this.havingPerm.remove(player.getName());
            }
        }
    }

}
