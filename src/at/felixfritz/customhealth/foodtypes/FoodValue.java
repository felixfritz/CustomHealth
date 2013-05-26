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
	
	private String foodName;
	private int regenHearts;
	private int regenHunger;
	private float saturation;
	private List<EffectValue> effects;
	
	/**
	 * Set it to a specific material. It will regenerate 0 hearts and 0 hunger bars!
	 * @param foodName
	 */
	public FoodValue(String foodName) {
		new FoodValue(foodName, 0, 0, 0, new ArrayList<EffectValue>());
	}
	
	public FoodValue(String foodName, int regenHearts, int regenHunger) {
		new FoodValue(foodName, regenHearts, regenHunger, 0, new ArrayList<EffectValue>());
	}
	
	public FoodValue(String foodName, int regenHearts, int regenHunger, float saturation) {
		new FoodValue(foodName, regenHearts, regenHunger, saturation, new ArrayList<EffectValue>());
	}
	
	public FoodValue(String foodName, int regenHearts, int regenHunger, float saturation, List<EffectValue> effects) {
		this.foodName = foodName;
		this.regenHearts = regenHearts;
		this.regenHunger = regenHunger;
		this.effects = effects;
	}
	
	
	/**
	 * Get the name of the food
	 * @return foodName
	 */
	public String getName() {
		return foodName;
	}
	
	
	/**
	 * Get the food as a material
	 * @return material
	 */
	public Material getFood() {
		return Material.valueOf((foodName.equalsIgnoreCase("enchanted_golden_apple")) ? "GOLDEN_APPLE" : foodName);
	}
	
	
	/**
	 * Get the hearts regenerated when eating the food
	 * @return regenHearts
	 */
	public int getRegenHearts() {
		return regenHearts;
	}
	
	
	/**
	 * Get the hunger regenerated when eating the food
	 * @return regenHunger
	 */
	public int getRegenHunger() {
		return regenHunger;
	}
	
	
	/**
	 * Get the saturation level when eating the food
	 * @return saturation
	 */
	public float getSaturation() {
		return saturation;
	}
	
	
	/**
	 * Set hearts regenerated
	 * @param regenHearts
	 */
	public void setRegenHearts(int regenHearts) {
		this.regenHearts = regenHearts;
	}
	
	
	/**
	 * Set hunger regenerated
	 * @param regenHunger
	 */
	public void setRegenHunger(int regenHunger) {
		this.regenHunger = regenHunger;
	}
	
	
	/**
	 * Set saturation level
	 * @param saturation
	 */
	public void setSaturation(float saturation) {
		this.saturation = saturation;
	}
	
	
	/**
	 * Set the effects
	 * @param effects
	 */
	public void setEffects(List<EffectValue> effects) {
		this.effects = effects;
	}
	
	
	/**
	 * Get the effects
	 * @return effects
	 */
	public List<EffectValue> getEffects() {
		return effects;
	}
	
	
	/**
	 * Add an effect
	 * @param effect
	 * @return false, when effect already exists
	 */
	public boolean addEffect(EffectValue effect) {
		for(EffectValue value : effects) {
			if(value.equals(effect))
				return false;
		}
		this.effects.add(effect);
		return true;
	}
	
	
	/**
	 * Remove effect
	 * @param effect
	 * @return false, if effect didn't exist
	 */
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
