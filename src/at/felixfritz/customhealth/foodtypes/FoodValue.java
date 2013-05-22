package at.felixfritz.customhealth.foodtypes;

import java.util.ArrayList;
import java.util.List;

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
	private List<EffectValue> effects;
	
	/**
	 * Set it to a specific material. It will regenerate 0 hearts and 0 hunger bars!
	 * @param foodName
	 */
	public FoodValue(Material foodName) {
		new FoodValue(foodName, regenHearts, regenHunger, new ArrayList<EffectValue>());
	}
	
	public FoodValue(Material foodName, int regenHearts, int regenHunger) {
		new FoodValue(foodName, regenHearts, regenHunger, new ArrayList<EffectValue>());
	}
	
	public FoodValue(Material foodName, int regenHearts, int regenHunger, List<EffectValue> effects) {
		this.foodName = foodName;
		this.regenHearts = regenHearts;
		this.regenHunger = regenHunger;
		this.effects = effects;
	}
	
	public String getName() {
		return this.foodName.name();
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
	
	public void setEffects(List<EffectValue> effects) {
		this.effects = effects;
	}
	
	public List<EffectValue> getEffects() {
		return effects;
	}
	
	public void addEffect(EffectValue effect) {
		this.effects.add(effect);
	}
	
	public boolean removeEffect(EffectValue effect) {
		for(EffectValue value : effects) {
			if(value.equals(effect)) {
				effects.remove(effect);
				return true;
			}
		}
		return false;
	}
}
