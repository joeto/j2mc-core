package to.joe.j2mc.core.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

/**
 * Abstract class from which all j2 commands come
 */
public abstract class MasterCommand implements TabExecutor {
    protected JavaPlugin plugin;

    public MasterCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String commandName = command.getName().toLowerCase();
        Player player = null;
        final boolean isPlayer = (sender instanceof Player);
        if (isPlayer) {
            player = (Player) sender;
        }

        this.exec(sender, commandName, args, player, isPlayer);

        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return new ArrayList<String>();
        }
        
        try {
            return J2MC_Manager.getVisibility().getPotentialMatches(args[(args.length - 1)], sender);
        } catch (BadPlayerMatchException e) {
            return new ArrayList<String>();
        }
    }

}