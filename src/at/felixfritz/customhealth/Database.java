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
 * @since 0.6
 * @version 0.7
 */
public class Database {
	
	//List of FoodValues, keys are the lists of worlds, values are the FoodValues
	//protected static Map<String, List<FoodValue>> effects;
	
	//Max food level for certain worlds. If player oversteps maximum of food-levels, if gets pushed down to the value (Integer) again
	protected static Map<String, Integer> maxFoodLevel;
	
	protected static Map<String, Integer> maxHeartLevel;
	
	//protected static List<PlayerDeathValues> deathValues;
	
	//I don't want anyone to create an object of the database
	private Database(){}
	
	
	/**
	 * Initialize maps. Only accessible from CustomHealth class
	 */
	protected static void initialize() {
		//effects = new HashMap<String, List<FoodValue>>();
		maxFoodLevel = new HashMap<String, Integer>();
		maxHeartLevel = new HashMap<String, Integer>();
	}
	
	/**
	 * Free maps (avoid memory leaks). Only accessible from CustomHealth class
	 */
	protected static void free() {
		//effects = null;
		maxFoodLevel = null;
		maxHeartLevel = null;
	}
	
	/**
	 * Get food value for the item in the world the player's standing in. Includes the data, which is deprecated for some reason.
	 * @param world
	 * @param item (which is hopefully edible)
	 * @return FoodValue of item in the world
	 */
	/*public static FoodValue getFoodValue(World world, ItemStack item) {
		if(item == null)
			return null;
		
		if(effects.containsKey(world.getName())) {
			List<FoodValue> ol = effects.get(world.getName());
			int x;
			if((x = ol.indexOf(new FoodValue(item.getType(), item.getDurability()))) >= 0)
				return ol.get(x);
			return null;
		}
		
		return null;
	}*/
	
	/**
	 * Check, if the world named "input" is registered in the FoodValues-Map.
	 * @param name
	 * @return true, if it does exist.
	 */
	/*public static boolean hasWorld(String name) {
		return effects.containsKey(name);
	}*/
	
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
	
	
	public static int getMaxHeartLevel(World world) {
		if(maxHeartLevel.containsKey(world.getName()))
			return maxHeartLevel.get(world.getName());
		return -1;
	}
	
	
	
	/*private static class PlayerDeathValues {
		
		private String name;
		private double hearts;
		private float saturation;
		
		public PlayerDeathValues(String name, double hearts, float saturation) {
			this.name = name;
			this.hearts = hearts;
			this.saturation = saturation;
		}
		
		public String getName() {
			return name;
		}
		
		public double getHearts() {
			return hearts;
		}
		
		public void setHearts(double hearts) {
			this.hearts = hearts;
		}
		
		public float getSaturation() {
			return saturation;
		}
		
		public void setSaturation(float saturation) {
			this.saturation = saturation;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof String)
				return o.equals(name);
			if(o instanceof Player)
				return ((Player) o).getName().equals(name);
			return false;
		}
	}*/
}
