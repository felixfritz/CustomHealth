package at.felixfritz.customhealth.foodtypes;

import org.bukkit.Material;

/**
 * The FoodValue: An object, that contains the material, 
 * the amount of hearts it regenerates and the amount of hunger bars it fills up
 * @author felixfritz
 *
 */
public class FoodValue {
	
	private Material foodName;
	private int regenHearts;
	private int regenHunger;
	
	/**
	 * Set it to a specific material. It will regenerate 0 hearts and 0 hunger bars!
	 * @param foodName
	 */
	public FoodValue(Material foodName) {
		this.foodName = foodName;
		regenHearts = 0;
		regenHunger = 0;
	}
	
	public FoodValue(Material foodName, int regenHearts, int regenHunger) {
		this.foodName = foodName;
		this.regenHearts = regenHearts;
		this.regenHunger = regenHunger;
	}
	
	
	
	public String getName() {
		return this.foodName.toString();
	}
	
	public Material getFood() {
		return this.foodName;
	}
	
	public int getRegenHearts() {
		return this.regenHearts;
	}
	
	public int getRegenHunger() {
		return this.regenHunger;
	}
	
	public void setRegenHearts(int regenHearts) {
		this.regenHearts = regenHearts;
	}
	
	public void setRegenHunger(int regenHunger) {
		this.regenHunger = regenHunger;
	}
}
