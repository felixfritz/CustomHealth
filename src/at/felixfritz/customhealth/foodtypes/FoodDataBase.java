package at.felixfritz.customhealth.foodtypes;

import java.util.LinkedList;
import java.util.List;

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
		foods = new LinkedList<FoodValue>();
		this.cfg = cfg;
		
		/*
		 * Loop through each material from the Material enum. If it's edible, add it to the list
		 */
		for(Material mat : Material.values()) {
			if(mat.isEdible())
				add(mat);
		}
	}
	
	private void add(Material food) {
		foods.add(new FoodValue(food, cfg.getInt("food." + food.toString().toLowerCase() + ".hearts"), cfg.getInt(
				"food." + food.toString().toLowerCase() + ".food")));
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
}
