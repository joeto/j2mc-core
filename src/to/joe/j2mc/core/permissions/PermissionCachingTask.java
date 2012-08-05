package to.joe.j2mc.core.permissions;

import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Core;

public class PermissionCachingTask implements Runnable {
    
    private J2MC_Core plugin;
    private Permissions permissions;
    
    public PermissionCachingTask(J2MC_Core plugin, Permissions permissions) {
        this.plugin = plugin;
        this.permissions = permissions;
    }
    
    @Override
    public void run() {
        permissions.setPlayerCache(plugin.getServer().getOnlinePlayers());
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("j2mc.core.admin")) {
                permissions.adminCache.add(player.getName());
            } else {
                permissions.adminCache.remove(player.getName());
            }
        }
    }
    
    
}
