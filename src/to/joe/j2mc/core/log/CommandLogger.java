package to.joe.j2mc.core.log;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import to.joe.j2mc.core.J2MC_Core;

/**
 * @deprecated
 * @author Admin
 *
 */
public class CommandLogger implements Listener {

    J2MC_Core core;

    public CommandLogger(J2MC_Core Core) {
        this.core = Core;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreCommandProcess(PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage();
        if (event.isCancelled()) {
            this.core.getLogger().info(event.getPlayer().getName() + " used command: " + message + " (Command was cancelled)");
            return;
        }
        this.core.getLogger().info(event.getPlayer().getName() + " used command: " + message);
    }

}
