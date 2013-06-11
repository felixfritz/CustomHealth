package at.felixfritz.customhealth.foodtypes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.util.Converter;
import at.felixfritz.customhealth.util.FloatValue;
import at.felixfritz.customhealth.util.IntValue;
import at.felixfritz.customhealth.util.UselessMath;

/**
 * The FoodDataBase: The place where every eatable food is stored and made available for everyone!
 * @author felixfritz
 */
public class FoodDataBase {
	
	//this list is available for everyone from everywhere!
	public static Map<World, List<FoodValue>> foods;
	
	/**
	 * Constructor with the FileConfiguration
	 * @param cfg
	 */
	public FoodDataBase(FileConfiguration cfg) {
		foods = new HashMap<World, List<FoodValue>>();
		List<World> worlds = Bukkit.getServer().getWorlds();
		
		String resource = CustomHealth.getResourcePath() + "worlds/";
		
		if(!new File(resource).exists())
			new File(resource).mkdir();
		
		for(World world : worlds) {
			addWorld(world);
		}
	}
	
	
	public static void addWorld(World world) {
		String resource = CustomHealth.getResourcePath();
		File worldFile = new File(resource + "worlds/" + world.getName() + ".yml");
		
		if(!worldFile.exists()) {
			CustomHealth.getPlugin().saveResource("template0x0159.yml", true);
			new File(resource + "template0x0159.yml").renameTo(worldFile);
		}
		
		List<FoodValue> values = getValues(worldFile);
		foods.put(world, values);
	}
	
	
	private static List<FoodValue> getValues(File file) {
		List<FoodValue> list = new ArrayList<FoodValue>();
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(Material mat : Material.values()) {
			if(mat.isEdible())
				list.add(getValue(mat.name(), config));
		}
		
		list.add(getValue("cake_block", config));
		list.add(getValue("enchanted_golden_apple", config));
		
		return list;
	}
	
	
	/**
	 * Add a food type to the food list
	 * @param food
	 */
	private static FoodValue getValue(String food, YamlConfiguration config) {
		
		food = food.toLowerCase();
		String path = "food." + food;
		int[] regenHeartsInt = UselessMath.stringToIntArray(config.getString(path + ".hearts"));
		int[] regenHungerInt = UselessMath.stringToIntArray(config.getString(path + ".food"));
		float[] saturationFloat = UselessMath.stringToFloatArray(config.getString(path + ".saturation"));
		
		IntValue regenHeartsValue = Converter.stringToIntValue(config.getString(path + ".hearts"));
		IntValue regenHungerValue = Converter.stringToIntValue(config.getString(path + ".hearts"));
		FloatValue saturationValue = Converter.stringToFloatValue(config.getString(path + ".hearts"));
		
		if(regenHeartsInt.length == 0) {
			CustomHealth.displayErrorMessage("Couldn't convert " + config.getString(path + ".hearts") + " for " + food + "!");
			CustomHealth.displayErrorMessage("Setting the hearts value of " + food + " to 0!");
			regenHeartsValue = new IntValue();
		} else
			regenHeartsValue = new IntValue(regenHeartsInt);
		
		
		if(regenHungerInt.length == 0) {
			CustomHealth.displayErrorMessage("Couldn't convert " + config.getString(path + ".food") + " for " + food + "!");
			CustomHealth.displayErrorMessage("Setting the hunger value of " + food + " to " + config.getInt(path + ".food") + "!");
			regenHungerValue = new IntValue(config.getInt(path + ".food"));
		} else
			regenHungerValue = new IntValue(regenHungerInt);
		
		
		if(saturationFloat.length == 0) {
			CustomHealth.displayErrorMessage("Couldn't convert " + config.getString(path + ".saturation") + " for " + food + "!");
			CustomHealth.displayErrorMessage("Setting saturation level of " + food + " to " + config.getDouble(path + ".saturation") + "!");
			saturationValue = new FloatValue((float) config.getDouble(path + ".saturation"));
		} else
			saturationValue = new FloatValue(saturationFloat);
		
		
		//Check, if the food has any effects yet
		String effects = config.getString(path + ".effects");
		List<EffectValue> effectList = null;
		if(effects != null && !effects.equalsIgnoreCase("none"))
			effectList = getPotionEffects(effects.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(";"));
		
		FoodValue value = new FoodValue(food);
		value.setRegenHearts(regenHeartsValue);
		value.setRegenHunger(regenHungerValue);
		value.setSaturation(saturationValue);
		value.setEffects(effectList);
		
		return value;
	}
	
	
	/**
	 * Some classes require all food names as a string. This is the method they're calling
	 * @return array
	 */
	public static String[] getFoodNames() {
		String[] names = new String[foods.size()];
		
		for(int x = 0; x < foods.size(); x++) {
			names[x] = foods.get(0).get(x).getName();
		}
		
		return names;
	}
	
	
	/**
	 * Get the food value from name
	 * @param name
	 * @return food value
	 */
	public static FoodValue getFoodValue(World world, String name) {
		List<FoodValue> list = foods.get(world);
		for(int x = 0; x < foods.size(); x++) {
			if(list.get(x).getName().equalsIgnoreCase(name))
				return list.get(x);
		}
		
		return null;
	}
	
	
	
	/**
	 * Get all the potion effects of type food from the config file and convert them into a EffectValue object
	 * @param effects, eg. 1,40%,10,2   -> type,probability,duration,strength
	 * @return list of all the effects
	 */
	public static List<EffectValue> getPotionEffects(String[] effects) {
		
		List<EffectValue> effectValues = new ArrayList<EffectValue>();
		
		EffectValue value;
		
		//Go through each effect in the given effects array
		for(String effect : effects) {
			
			value = new EffectValue();
			
			String[] tmp = effect.split(",");
			switch(tmp.length) {
			case 4:
				try {
					value.setStrength(new IntValue(UselessMath.stringToIntArray(tmp[3])));
				} catch(NumberFormatException e) {
					CustomHealth.displayErrorMessage(tmp[3] + " is not an integer!");
					continue;
				} catch(Exception e) {
					CustomHealth.displayErrorMessage("Something went wrong when using " + tmp[3] + ".");
					e.printStackTrace();
				}
			case 3:
				try {
					value.setDuration(new IntValue(UselessMath.stringToIntArray(tmp[2])));
				} catch(NumberFormatException e) {
					CustomHealth.displayErrorMessage(tmp[2] + " is not an integer!");
					continue;
				} catch(Exception e) {
					CustomHealth.displayErrorMessage("Something went wrong when using " + tmp[2] + ".");
					e.printStackTrace();
				}
			case 2:
				try {
					value.setProbability(new FloatValue(UselessMath.stringToFloatArray(tmp[1].replaceAll("%", ""))).divideBy(100));
				} catch(NumberFormatException e) {
					CustomHealth.displayErrorMessage(tmp[1] + " is not an integer!");
					continue;
				} catch(Exception e) {
					CustomHealth.displayErrorMessage("Something went wrong when using " + tmp[1] + ".");
					e.printStackTrace();
				}
			case 1:
				try {
					value.setEffect(Integer.valueOf(tmp[0]));
				} catch(NumberFormatException e) {
					CustomHealth.displayErrorMessage(tmp[0] + " is not a valid number.");
					continue;
				} catch(Exception e) {
					CustomHealth.displayErrorMessage("Something went wrong when using " + tmp[0] + ".");
					e.printStackTrace();
					continue;
				}
			}
			
			if(!value.hasValidEffect())
				continue;
			
			effectValues.add(value);
		}
		
		if(effectValues.size() == 0)
			return null;
		else
			return effectValues;
	}
}
