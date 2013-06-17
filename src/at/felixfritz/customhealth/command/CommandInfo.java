package at.felixfritz.customhealth.command;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;
import at.felixfritz.customhealth.foodtypes.FoodValue;
import at.felixfritz.customhealth.util.Converter;

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
	public static void informPlayer(Player sender, ItemStack stack) {
		
		//Get string message from the config
		FileConfiguration cfg = CustomHealth.getPlugin().getConfig();
		
		try {
			String name = Converter.itemStackToString(stack);
			//Get food from the material. It must be edible, the CustomCommand class automatically checks, if it's edible
			FoodValue foodValue = FoodDataBase.getFoodValue(sender.getWorld(), name);
			
			String regenHearts = String.valueOf(foodValue.getRegenHearts());
			String regenFood = String.valueOf(foodValue.getRegenHunger());
			String saturation = String.valueOf(foodValue.getSaturation());
			StringBuilder effects = new StringBuilder();
			
			if(foodValue.getEffects() == null || foodValue.getEffects().size() == 0) {
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
