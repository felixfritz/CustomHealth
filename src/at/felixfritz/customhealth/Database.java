package at.felixfritz.customhealth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.foodtypes.FoodValue;

/**
 * The Database class contains 2 static maps with informations about
 * the FoodValues and the maximum of food levels.
 * 
 * Only main class (CustomHealth) is able to completely mess with this class,
 * outsiders only get to see things they're allowed to see.
 * @author felixfritz
 * @version 0.6
 */
public class Database {
	
	//List of FoodValues, keys are the lists of worlds, values are the FoodValues
	protected static Map<List<String>, List<FoodValue>> effects;
	
	//Max food level for certain worlds. If player oversteps maximum of food-levels, if gets pushed down to the value (Integer) again
	protected static Map<String, Integer> maxFoodLevel;
	
	//I don't want anyone to create an object of the database
	private Database(){}
	
	
	/**
	 * Initialize maps. Only accessible from CustomHealth class
	 */
	protected static void initialize() {
		effects = new HashMap<List<String>, List<FoodValue>>();
		maxFoodLevel = new HashMap<String, Integer>();
	}
	
	/**
	 * Free maps (avoid memory leaks). Only accessible from CustomHealth class
	 */
	protected static void free() {
		effects = null;
		maxFoodLevel = null;
	}
	
	/**
	 * Get food value for the item in the world the player's standing in. Includes the data, which is deprecated for some reason.
	 * @param world
	 * @param item (which is hopefully edible)
	 * @return FoodValue of item in the world
	 */
	@SuppressWarnings("deprecation")
	public static FoodValue getFoodValue(World world, ItemStack item) {
		if(item == null)
			return null;
		
		for(List<String> list : effects.keySet()) {
			if(list.contains(world.getName())) {
				List<FoodValue> ol = effects.get(list);
				if(ol.indexOf(new FoodValue(item.getType(), item.getData().getData())) >= 0)
					return ol.get(ol.indexOf(new FoodValue(item.getType(), item.getData().getData())));
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Check, if the world named "input" is registered in the FoodValues-Map.
	 * @param name
	 * @return true, if it does exist.
	 */
	public static boolean hasWorld(String name) {
		for(List<String> list : effects.keySet()) {
			if(effects.get(list).contains(name))
				return true;
		}
		return false;
	}
	
	/**
	 * Get the max amount of food bars that can be filled up in that world
	 * @param world
	 * @return -1, if there's no limitation / the world is not in there
	 */
	public static int getMaxFoodLevel(World world) {
		if(maxFoodLevel.containsKey(world.getName()))
			return maxFoodLevel.get(world.getName());
		return -1;
	}
	
}
