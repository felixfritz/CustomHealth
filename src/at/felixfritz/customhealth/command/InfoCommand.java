package at.felixfritz.customhealth.command;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Material;

public class InfoCommand {
	
	public static void informPlayer(CommandSender sender, Material mat) {
		
		FileConfiguration cfg = sender.getServer().getPluginManager().getPlugin("CustomHealth").getConfig();
		
		try {
			String food = mat.name();
			int regenHearts = cfg.getInt("food." + mat.name().toLowerCase() + ".hearts");
			int regenFood = cfg.getInt("food." + mat.name().toLowerCase() + ".food");
			
			Messenger.sendMessage(cfg.getString("messages.food-info").replaceAll("<food>", food).
					replaceAll("<hearts>", String.valueOf(regenHearts)).replaceAll("<hunger>", String.valueOf(regenFood)).
					replaceAll("<effects>", cfg.getString("food." + mat.name().toLowerCase() + ".effects")), sender);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
