package to.joe.j2mc.core.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IRCMessageEvent extends Event{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1947500892668582301L;
	private static final HandlerList handlers = new HandlerList();
	private boolean adminChannel;
	private String message;
	
	public IRCMessageEvent(boolean adminChannel, String message){
		this.adminChannel = adminChannel;
		this.message = message;
	}
	
	/**
	 * Gets the message to be sent over irc
	 * @return
	 */
	public String getMessage(){
		return this.message;
	}
	
	/**
	 * True if message should be sent to admin channel
	 * @return
	 */
	public boolean toAdminChannel(){
		return this.adminChannel;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	
}
