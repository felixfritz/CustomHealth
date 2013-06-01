package at.felixfritz.customhealth.foodtypes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The FoodDataBase: The place where every eatable food is stored and made available for everyone!
 * @author felixfritz
 */
public class FoodDataBase {
	
	//this list is available for everyone from everywhere!
	public static List<FoodValue> foods;
	
	//not like this one
	private FileConfiguration cfg;
	
	/**
	 * Constructor with the FileConfiguration
	 * @param cfg
	 */
	public FoodDataBase(FileConfiguration cfg) {
		foods = new ArrayList<FoodValue>();
		this.cfg = cfg;
		
		/*
		 * Loop through each material from the Material enum. If it's edible, add it to the list
		 */
		for(Material mat : Material.values()) {
			if(mat.isEdible())
				add(mat.name());
		}
		
		add("cake_block");
		add("enchanted_golden_apple");
	}
	
	
	/**
	 * Add a food type to the food list
	 * @param food
	 */
	private void add(String food) {
		
		food = food.toLowerCase();
		String path = "food." + food;
		int regenHearts = cfg.getInt(path + ".hearts");
		int regenHunger = cfg.getInt(path + ".food");
		float saturation = (float) cfg.getDouble(path + ".saturation");
		
		//Check, if the food has any effects yet
		String effects = cfg.getString(path + ".effects");
		List<EffectValue> effectList = null;
		if(effects != null && !effects.equalsIgnoreCase("none"))
			effectList = getPotionEffects(effects.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(";"));
		
		FoodValue value = new FoodValue(food);
		value.setRegenHearts(regenHearts);
		value.setRegenHunger(regenHunger);
		value.setSaturation(saturation);
		value.setEffects(effectList);
		
		foods.add(value);
	}
	
	
	/**
	 * Some classes require all food names as a string. This is the method they're calling
	 * @return array
	 */
	public static String[] getFoodNames() {
		String[] names = new String[foods.size()];
		
		for(int x = 0; x < foods.size(); x++) {
			names[x] = foods.get(x).getName();
		}
		
		return names;
	}
	
	
	/**
	 * Get the food value from name
	 * @param name
	 * @return food value
	 */
	public static FoodValue getFoodValue(String name) {
		for(int x = 0; x < foods.size(); x++) {
			if(foods.get(x).getName().equalsIgnoreCase(name))
				return foods.get(x);
		}
		
		return null;
	}
	
	
	
	/**
	 * Get all the potion effects of type food from the config file and convert them into a EffectValue object
	 * @param effects, eg. 1,40%,10,2   -> type,probability,duration,strength
	 * @return list of all the effects
	 */
	public List<EffectValue> getPotionEffects(String[] effects) {
		
		List<EffectValue> effectValues = new ArrayList<EffectValue>();
		
		int effectInt;		//Can be either a number or a word (e.g. BLINDNESS)
		float effectProbability;	//Number between 0% and 100%
		int effectDuration;			//In seconds
		int effectStrength;
		
		//Go through each effect in the given effects array
		for(String effect : effects) {
			
			//Set some values, in case the array doesn't provide us with enough information
			effectInt = -1;			//If there's no effect given, which will be checked later on, nothing happens
			effectProbability = 1;	//Effect probability set to 100%
			effectDuration = 30;	//Duration set to 30 seconds
			effectStrength = 0;		//Normal strength
			
			String[] tmp = effect.split(",");
			switch(tmp.length) {
			case 4:
				try {
					effectStrength = Integer.valueOf(tmp[3]) - 1;
				} catch(NumberFormatException e) {
					System.out.println(ChatColor.RED + tmp[3] + " is not an integer!");
					continue;
				} catch(Exception e) {
					System.out.println(ChatColor.RED + "Something went wrong when using " + tmp[3] + ".");
					e.printStackTrace();
				}
			case 3:
				try {
					effectDuration = Integer.valueOf(tmp[2]);
				} catch(NumberFormatException e) {
					System.out.println(ChatColor.RED + tmp[2] + " is not an integer!");
					continue;
				} catch(Exception e) {
					System.out.println(ChatColor.RED + "Something went wrong when using " + tmp[2] + ".");
					e.printStackTrace();
				}
			case 2:
				try {
					effectProbability = Float.valueOf(tmp[1].replaceAll("%", "")) / 100;
				} catch(NumberFormatException e) {
					System.out.println(ChatColor.RED + tmp[1] + " is not an integer!");
					continue;
				} catch(Exception e) {
					System.out.println(ChatColor.RED + "Something went wrong when using " + tmp[1] + ".");
					e.printStackTrace();
				}
			case 1:
				try {
					effectInt = Integer.valueOf(tmp[0]);
				} catch(NumberFormatException e) {
					System.out.println(effectInt + " is not a valid number.");
					continue;
				} catch(Exception e) {
					System.out.println("Something went wrong when using " + tmp[0] + ".");
					e.printStackTrace();
					continue;
				}
			}
			
			if(effectInt < 0 || effectInt > 20)
				continue;
			
			effectValues.add(new EffectValue(effectInt, effectProbability, effectDuration, effectStrength));
		}
		
		if(effectValues.size() == 0)
			return null;
		else
			return effectValues;
	}
}
