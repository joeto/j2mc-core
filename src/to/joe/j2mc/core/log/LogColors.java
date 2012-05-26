package to.joe.j2mc.core.log;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class LogColors {
    static final char ESC = 27;
    private static final Map<ChatColor, String> replacements;
    private static final ChatColor[] colors;
    static {
        colors = ChatColor.values();
        replacements = new EnumMap<ChatColor, String>(ChatColor.class);
        LogColors.replacements.put(ChatColor.BLACK, attrib(0));
        LogColors.replacements.put(ChatColor.DARK_BLUE, attrib(34));
        LogColors.replacements.put(ChatColor.DARK_GREEN, attrib(32));
        LogColors.replacements.put(ChatColor.DARK_AQUA, attrib(36));
        LogColors.replacements.put(ChatColor.DARK_RED, attrib(31));
        LogColors.replacements.put(ChatColor.DARK_PURPLE, attrib(35));
        LogColors.replacements.put(ChatColor.GOLD, attrib(33));
        LogColors.replacements.put(ChatColor.GRAY, attrib(37));
        LogColors.replacements.put(ChatColor.DARK_GRAY, attrib(0));
        LogColors.replacements.put(ChatColor.BLUE, attrib(34));
        LogColors.replacements.put(ChatColor.GREEN, attrib(32));
        LogColors.replacements.put(ChatColor.AQUA, attrib(36));
        LogColors.replacements.put(ChatColor.RED, attrib(31));
        LogColors.replacements.put(ChatColor.LIGHT_PURPLE, attrib(35));
        LogColors.replacements.put(ChatColor.YELLOW, attrib(33));
        LogColors.replacements.put(ChatColor.WHITE, attrib(37));
    }

    public static String process(String result) {
        for (final ChatColor color : LogColors.colors) {
            if (LogColors.replacements.containsKey(color)) {
                result = result.replaceAll(color.toString(), LogColors.replacements.get(color));
            } else {
                result = result.replaceAll(color.toString(), "");
            }
        }
        return result + attrib(0);
    }

    private static String attrib(int attr) {
        return ESC + "[" + attr + "m";
    }
}
