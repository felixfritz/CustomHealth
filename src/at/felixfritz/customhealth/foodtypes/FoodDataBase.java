package at.felixfritz.customhealth.foodtypes;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class FoodDataBase {
	
	public static List<FoodValue> foods;
	private FileConfiguration cfg;
	
	public FoodDataBase(FileConfiguration cfg) {
		foods = new LinkedList<FoodValue>();
		this.cfg = cfg;
		for(Material mat : Material.values()) {
			if(mat.isEdible())
				add(mat);
		}
	}
	
	private void add(Material food) {
		foods.add(new FoodValue(food, cfg.getInt("food." + food.toString().toLowerCase() + ".hearts"), cfg.getInt(
				"food." + food.toString().toLowerCase() + ".food")));
	}
	
	public static String[] getFoodNames() {
		String[] names = new String[foods.size()];
		
		for(int x = 0; x < foods.size(); x++) {
			names[x] = foods.get(x).getName();
		}
		
		return names;
	}
}
