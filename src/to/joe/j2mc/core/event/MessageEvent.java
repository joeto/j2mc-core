package to.joe.j2mc.core.event;

import java.util.HashSet;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MessageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Return a HashSet of Strings submitted
     * 
     * @param strings
     * @return
     */
    public static HashSet<String> compile(String... strings) {
        final HashSet<String> set = new HashSet<String>();
        for (final String string : strings) {
            set.add(string);
        }
        return set;
    }

    public static HandlerList getHandlerList() {
        return MessageEvent.handlers;
    }

    private final HashSet<String> targets;

    private final String message;

    public MessageEvent(HashSet<String> targets, String message) {
        this.targets = targets;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return MessageEvent.handlers;
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
    public boolean targetting(String... targets) {
        for (final String target : targets) {
            if (this.targets.contains(target)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns all targets
     * 
     */
    
    public HashSet<String> alltargets(){
    	return this.targets;
    }

}
