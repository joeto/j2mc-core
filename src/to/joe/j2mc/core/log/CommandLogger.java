package to.joe.j2mc.core.log;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import to.joe.j2mc.core.J2MC_Core;

public class CommandLogger implements Listener{

    J2MC_Core core;
    
    public CommandLogger(J2MC_Core Core){
        this.core = Core;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreCommandProcess(PlayerCommandPreprocessEvent event){
        String message = event.getMessage();
        if(event.isCancelled()){
            core.getLogger().info(event.getPlayer().getName() + " used command: " + message + " (Command was cancelled)");
        }
        core.getLogger().info(event.getPlayer().getName() + " used command: " + message);
    } 
    
}
