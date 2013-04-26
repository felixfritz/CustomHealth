package at.felixfritz.customhealth.foodtypes;

import org.bukkit.Material;

public class FoodValue {
	
	private Material foodName;
	private int regenHearts;
	private int regenHunger;
	
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
