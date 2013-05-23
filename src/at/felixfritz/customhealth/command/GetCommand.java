package at.felixfritz.customhealth.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;
import at.felixfritz.customhealth.foodtypes.FoodValue;

/**
 * Class only used for the static informPlayer method from the CustomCommand class.
 * @author felixfritz
 */
public class GetCommand {
	
	/**
	 * Inform the player about a food
	 * @param sender
	 * @param mat
	 */
	public static void informPlayer(CommandSender sender, Material mat) {
		
		//Get string message from the config
		FileConfiguration cfg = CustomHealth.getPlugin().getConfig();
		
		try {
			//Get food from the material. It must be edible, the CustomCommand class automatically checks, if it's edible
			FoodValue foodValue = FoodDataBase.getFoodValue(mat.name());
			
			int regenHearts = foodValue.getRegenHearts();
			int regenFood = foodValue.getRegenHunger();
			StringBuilder effects = new StringBuilder();
			
			if(foodValue.getEffects() == null) {
				effects.append("none");
			} else {
				for(EffectValue effect : foodValue.getEffects()) {
					effects.append(effect.getEffect().getName().toLowerCase());
					effects.append(", ");
				}
				effects.replace(effects.length() - 2, effects.length(), "");
			}
			
			//Inform the player over the Messenger class, replace all sorts of shortcuts with the actual parameter
			Messenger.sendMessage(cfg.getString("messages.food-info").replaceAll("<food>", foodValue.getName()).
					replaceAll("<hearts>", String.valueOf(regenHearts)).replaceAll("<hunger>", String.valueOf(regenFood)).
					replaceAll("<effects>", effects.toString()), sender);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
