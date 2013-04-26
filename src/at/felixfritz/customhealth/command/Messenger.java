package at.felixfritz.customhealth.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Only the Messenger class. Called by many other classes to send the player text with multiple lines.
 * @author felixfritz
 */
public class Messenger {
	
	
	/**
	 * Send message msg to the CommandSender sender
	 * @param msg
	 * @param sender
	 */
	public static void sendMessage(String msg, CommandSender sender) {
		//Split the whole message with the <n> shortcut that allows the player to create linebreaks.
		String[] messages = ChatColor.translateAlternateColorCodes('&', msg).split("<n>");
		
		for(String s : messages) {
			sender.sendMessage(s);
		}
	}
	
}
