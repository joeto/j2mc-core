package to.joe.j2mc.core;

import org.bukkit.Bukkit;

public class Debug {
    private static boolean enabled;

    public static void enable(boolean enable) {
        Debug.enabled = enable;
    }

    public static void log(String message) {
        if (!Debug.enabled) {
            return;
        }
        Bukkit.getLogger().info(message);
    }
}
