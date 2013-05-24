package at.felixfritz.customhealth.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import at.felixfritz.customhealth.CustomHealth;

public class CommandPlugin {
	
	public static boolean sendInfo(CommandSender sender) {
		ChatColor p = ChatColor.LIGHT_PURPLE;
		StringBuilder info = new StringBuilder();
		PluginDescriptionFile descr = CustomHealth.getPlugin().getDescription();
		
		info.append(p);
		info.append(descr.getName());
		info.append(" v");
		info.append(descr.getVersion());
		info.append(" by ");
		info.append(descr.getAuthors().get(0));
		info.append("<n>");
		info.append(p);
		info.append("Website: ");
		info.append(descr.getWebsite());
		
		Messenger.sendMessage(info.toString(), sender);
		
		return true;
	}
}
