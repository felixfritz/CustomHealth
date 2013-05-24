package at.felixfritz.customhealth.command;

import java.util.Map;

import org.bukkit.command.CommandSender;

import at.felixfritz.customhealth.CustomHealth;

public class CommandSet {
	
	/**
	 * Try to set the hearts and food values for the item
	 * @param sender, who will recieve the message if everything went well
	 * @param material that has to get the new value
	 * @param value, is a Map, contains a String (eg. "hearts" or "food") and a number between 0 and 20
	 */
	public static void executeSetCommand(CommandSender sender, String material, Map<String, Integer> value) {
		CustomHealth plugin = CustomHealth.getPlugin();
		
		for(String s : value.keySet()) {
			try {
				plugin.getConfig().set("food." + material.toLowerCase() + "." + s, value.get(s));
				sendMessage(sender, true, material, s, value.get(s));
			} catch(Exception e) {
				sendMessage(sender, false, material, s, value.get(s));
				e.printStackTrace();
			}
		}
		
		CustomHealth.reloadPlugin();
	}
	
	/**
	 * Send message, whether everything went as expected or not.
	 * @param sender, who will recieve the message
	 * @param succeeded, if so, send him a "hooray" message, otherwise don't
	 * @param food that the player tried to change
	 * @param type, either "hearts" or "food"
	 * @param amount, a number between 0 and 20
	 */
	public static void sendMessage(CommandSender sender, boolean succeeded, String food, String type, int amount) {
		String root = (succeeded) ? "messages.set-successful" : "messages.set-failed";
		String message = CustomHealth.getPlugin().getConfig().getString(root).replaceAll("<food>", food).replaceAll("<type>", type).replaceAll("<amount>", 
				String.valueOf(amount));
		Messenger.sendMessage(message, sender);
	}
}
