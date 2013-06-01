package at.felixfritz.customhealth.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import at.felixfritz.customhealth.CustomHealth;

public class CommandReset {
	
	/**
	 * Reset the config.yml file in the CustomHealth folder
	 * @param sender, to whom we'll send the message if everything went well
	 */
	public static boolean reset(CommandSender sender) {
		//Message that everything is being resetted
		Messenger.sendMessage(ChatColor.GREEN + "Resetting everything...", sender);
		
		//Overwrite the config.yml file in the CustomHealth folder
		CustomHealth.getPlugin().saveResource("config.yml", /* Overwrite it? */ true);
		CustomHealth.reloadPlugin();
		
		//Tell the user that everything went successful
		Messenger.sendMessage(ChatColor.GREEN + "Done.", sender);
		
		return true;
	}
}
