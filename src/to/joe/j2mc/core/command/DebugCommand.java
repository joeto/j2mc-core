package to.joe.j2mc.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.MySQL.MySQLConnectionPool.JDCConnection;

public class DebugCommand extends MasterCommand {

    J2MC_Core plugin;

    public DebugCommand(J2MC_Core core) {
        super(core);
        this.plugin = core;
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        sender.sendMessage(ChatColor.GOLD + "=========[MySQL]=========");
        JDCConnection[] connections = J2MC_Manager.getMySQL().getPool().getConnections();
        for (int i = 0; i < connections.length; i++) {
            int load = connections[i].getLoad();
            String loadString;
            if (load < 10) {
                loadString = "" + ChatColor.GREEN + load;
            } else if (load < 30) {
                loadString = "" + ChatColor.YELLOW + load;
            } else {
                loadString = "" + ChatColor.DARK_RED + load;
            }
            sender.sendMessage("" + ChatColor.BLUE + ChatColor.BOLD + "Connection " + i + ": " + loadString);
        }
        sender.sendMessage(ChatColor.GOLD + "========================");
    }

}
