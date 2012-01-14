package to.joe.j2mc.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abstract class from which all j2 commands come
 */
public abstract class MasterCommand implements CommandExecutor {
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

}