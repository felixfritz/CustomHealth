package at.felixfritz.customhealth.command;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.*;

/**
 * Class only used for the static informPlayer method from the CustomCommand class.
 * @author felixfritz
 */
public class CommandInfo {
	
	/**
	 * Inform the player about a food
	 * @param sender
	 * @param stack
	 */
	public static void informPlayer(CommandSender sender, ItemStack stack) {
		
		//Get string message from the config
		FileConfiguration cfg = CustomHealth.getPlugin().getConfig();
		
		try {
			String name = (stack.getData().getData() == 1) ? "ENCHANTED_GOLDEN_APPLE" : stack.getType().name();
			//Get food from the material. It must be edible, the CustomCommand class automatically checks, if it's edible
			FoodValue foodValue = FoodDataBase.getFoodValue(name);
			
			String regenHearts = String.valueOf(foodValue.getRegenHearts());
			String regenFood = String.valueOf(foodValue.getRegenHunger());
			String saturation = String.valueOf(foodValue.getSaturation());
			StringBuilder effects = new StringBuilder();
			
			if(foodValue.getEffects() == null) {
				effects.append("none");
			} else {
				for(EffectValue effect : foodValue.getEffects()) {
					effects.append(effect.getName());
					effects.append(", ");
				}
				effects.replace(effects.length() - 2, effects.length(), "");
			}
			
			//Inform the player over the Messenger class, replace all sorts of shortcuts with the actual parameter
			Messenger.sendMessage(cfg.getString("messages.food-info").replaceAll("<food>", foodValue.getName()).
					replaceAll("<hearts>", regenHearts).replaceAll("<hunger>", regenFood).replaceAll("<saturation>", saturation).
					replaceAll("<effects>", effects.toString()), sender);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
