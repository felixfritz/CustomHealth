package at.felixfritz.customhealth.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.FoodValue;

public class Converter {
	
	public static String itemStackToString(ItemStack stack) {
		
		if(stack.getType() == Material.GOLDEN_APPLE && stack.getData().getData() == 1)
			return "ENCHANTED_GOLDEN_APPLE";
		
		return stack.getType().name();
	}
	
	public static ItemStack stringToItemStack(String item) {
		
		ItemStack stack;
		if(item.equalsIgnoreCase("ENCHANTED_GOLDEN_APPLE")) {
			stack = new ItemStack(Material.GOLDEN_APPLE);
			stack.getData().setData((byte) 1);
		} else
			stack = new ItemStack(Material.valueOf(item.toUpperCase()));
		
		return stack;
	}
	
	public static FoodValue loreToFoodValue(List<String> lore) {
		
		FoodValue value = new FoodValue("unimportant");
		value.setRegenHearts(stringToIntValue(lore.get(0).substring(2, lore.get(0).length() - 7)));
		value.setRegenHunger(stringToIntValue(lore.get(1).substring(2, lore.get(1).length() - 10)));
		return value;
	}
	
	public static IntValue stringToIntValue(String word) {
		
		String[] stringValues = word.replaceAll(" ", "").split("/");
		
		try {
			if(stringValues.length == 1)
				return new IntValue(Integer.parseInt(stringValues[0]));
			return new IntValue(Integer.parseInt(stringValues[0]), Integer.parseInt(stringValues[1]));
		} catch(NumberFormatException e) {
			CustomHealth.displayErrorMessage("Couldn't convert " + word + " into a readable value!");
			CustomHealth.displayErrorMessage("Make sure to use the '/' character to define min and max values.");
			return null;
		} catch(Exception e) {
			CustomHealth.displayErrorMessage("There was a problem when reading the value for " + word + ".");
			e.printStackTrace();
			return null;
		}
	}
	
	public static FloatValue stringToFloatValue(String word) {
		
		String[] stringValues = word.replaceAll(" ", "").split("/");
		
		try {
			if(stringValues.length == 1)
				return new FloatValue(Float.parseFloat(stringValues[0]));
			return new FloatValue(Float.parseFloat(stringValues[0]), Float.parseFloat(stringValues[1]));
		} catch(NumberFormatException e) {
			CustomHealth.displayErrorMessage("Couldn't convert " + word + " into a readable value!");
			CustomHealth.displayErrorMessage("Make sure to use the '/' character to define min and max values.");
			return null;
		} catch(Exception e) {
			CustomHealth.displayErrorMessage("There was a problem when reading the value for " + word + ".");
			e.printStackTrace();
			return null;
		}
	}
}
