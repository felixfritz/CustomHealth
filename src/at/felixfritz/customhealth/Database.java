package at.felixfritz.customhealth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.foodtypes.FoodValue;

public class Database {
	
	protected static Map<List<String>, List<FoodValue>> effects;
	protected static Map<String, Integer> maxFoodLevel;
	
	private Database(){}
	
	protected static void initialize() {
		effects = new HashMap<List<String>, List<FoodValue>>();
		maxFoodLevel = new HashMap<String, Integer>();
	}
	
	protected static void free() {
		effects = null;
		maxFoodLevel = null;
	}
	
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
	
	public static boolean hasWorld(String name) {
		for(List<String> list : effects.keySet()) {
			if(effects.get(list).contains(name))
				return true;
		}
		return false;
	}
	
	public static int getMaxFoodLevel(World world) {
		if(maxFoodLevel.containsKey(world.getName()))
			return maxFoodLevel.get(world.getName());
		return -1;
	}
	
}
