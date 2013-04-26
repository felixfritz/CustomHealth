package at.felixfritz.customhealth.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messenger {
	
	public static void sendMessage(String msg, CommandSender sender) {
		String[] messages = ChatColor.translateAlternateColorCodes('&', msg).split("<n>");
		
		for(String s : messages) {
			sender.sendMessage(s);
		}
	}
	
}
