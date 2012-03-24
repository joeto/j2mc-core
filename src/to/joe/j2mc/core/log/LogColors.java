package to.joe.j2mc.core.log;

import java.util.EnumMap;
import java.util.Map;

import jline.ANSIBuffer.ANSICodes;

import org.bukkit.ChatColor;

public class LogColors {
    private static final Map<ChatColor, String> replacements;
    private static final ChatColor[] colors;
    static {
        colors = ChatColor.values();
        replacements = new EnumMap<ChatColor, String>(ChatColor.class);
        LogColors.replacements.put(ChatColor.BLACK, ANSICodes.attrib(0));
        LogColors.replacements.put(ChatColor.DARK_BLUE, ANSICodes.attrib(34));
        LogColors.replacements.put(ChatColor.DARK_GREEN, ANSICodes.attrib(32));
        LogColors.replacements.put(ChatColor.DARK_AQUA, ANSICodes.attrib(36));
        LogColors.replacements.put(ChatColor.DARK_RED, ANSICodes.attrib(31));
        LogColors.replacements.put(ChatColor.DARK_PURPLE, ANSICodes.attrib(35));
        LogColors.replacements.put(ChatColor.GOLD, ANSICodes.attrib(33));
        LogColors.replacements.put(ChatColor.GRAY, ANSICodes.attrib(37));
        LogColors.replacements.put(ChatColor.DARK_GRAY, ANSICodes.attrib(0));
        LogColors.replacements.put(ChatColor.BLUE, ANSICodes.attrib(34));
        LogColors.replacements.put(ChatColor.GREEN, ANSICodes.attrib(32));
        LogColors.replacements.put(ChatColor.AQUA, ANSICodes.attrib(36));
        LogColors.replacements.put(ChatColor.RED, ANSICodes.attrib(31));
        LogColors.replacements.put(ChatColor.LIGHT_PURPLE, ANSICodes.attrib(35));
        LogColors.replacements.put(ChatColor.YELLOW, ANSICodes.attrib(33));
        LogColors.replacements.put(ChatColor.WHITE, ANSICodes.attrib(37));
    }

    public static String process(String result) {
        for (final ChatColor color : LogColors.colors) {
            if (LogColors.replacements.containsKey(color)) {
                result = result.replaceAll(color.toString(), LogColors.replacements.get(color));
            } else {
                result = result.replaceAll(color.toString(), "");
            }
        }
        return result + ANSICodes.attrib(0);
    }
}
