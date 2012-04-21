package to.joe.j2mc.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;

public class ReloadPermissionsCommand extends MasterCommand{
    
    J2MC_Core plugin;

    public ReloadPermissionsCommand(J2MC_Core plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        sender.sendMessage(ChatColor.GOLD + "Reloading permissions.....");
        J2MC_Manager.getPermissions().loadGroupsAndPermissions();
        sender.sendMessage(ChatColor.GOLD + "Reloaded!");
    }

}
