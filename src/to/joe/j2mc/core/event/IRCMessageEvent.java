package to.joe.j2mc.core.event;

import java.util.HashSet;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IRCMessageEvent extends Event {

    private static final long serialVersionUID = 1L;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return IRCMessageEvent.handlers;
    }

    private final HashSet<String> targets;

    private final String message;

    public IRCMessageEvent(HashSet<String> targets, String message) {
        this.targets = targets;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCMessageEvent.handlers;
    }

    /**
     * Gets the message
     * 
     * @return
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Checks for a specific target
     * 
     * @param target
     * @return
     */
    public boolean targetting(String target) {
        return this.targets.contains(target);
    }

    /**
     * Checks for any target
     * 
     * @param targets
     * @return
     */
    public boolean targetting(String[] targets) {
        for (final String target : targets) {
            if (this.targets.contains(target)) {
                return true;
            }
        }
        return false;
    }

}
