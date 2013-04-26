package at.felixfritz.customhealth.command;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Material;

/**
 * Class only used for the static informPlayer method from the CustomCommand class.
 * @author felixfritz
 */
public class InfoCommand {
	
	/**
	 * Inform the player about a food
	 * @param sender
	 * @param mat
	 */
	public static void informPlayer(CommandSender sender, Material mat) {
		
		//Load the config that contains all the numbers and settings
		FileConfiguration cfg = sender.getServer().getPluginManager().getPlugin("CustomHealth").getConfig();
		
		try {
			//Get food from the material. It must be edible, the CustomCommand class automatically checks, if it's edible
			String food = mat.name().toLowerCase();
			int regenHearts = cfg.getInt("food." + mat.name() + ".hearts");
			int regenFood = cfg.getInt("food." + mat.name() + ".food");
			
			//Inform the player over the Messenger class, replace all sorts of shortcuts with the actual parameter
			Messenger.sendMessage(cfg.getString("messages.food-info").replaceAll("<food>", food).
					replaceAll("<hearts>", String.valueOf(regenHearts)).replaceAll("<hunger>", String.valueOf(regenFood)).
					replaceAll("<effects>", cfg.getString("food." + mat.name().toLowerCase() + ".effects")), sender);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
