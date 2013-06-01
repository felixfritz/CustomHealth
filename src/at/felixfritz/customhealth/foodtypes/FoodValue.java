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
	
	private String name;
	private int regenHearts = 0;
	private int regenHunger = 0;
	private float saturation = 0;
	private List<EffectValue> effects = new ArrayList<EffectValue>();
	
	/**
	 * Set it to a specific material. It will regenerate 0 hearts and 0 hunger bars!
	 * @param foodName
	 */
	public FoodValue(String foodName) {
		setName(foodName);
	}
	
	public FoodValue(String foodName, int regenHearts, int regenHunger) {
		setName(foodName);
		setRegenHearts(regenHearts);
		setRegenHunger(regenHunger);
	}
	
	public FoodValue(String foodName, int regenHearts, int regenHunger, float saturation) {
		setName(foodName);
		setRegenHearts(regenHearts);
		setRegenHunger(regenHunger);
		setSaturation(saturation);
	}
	
	public FoodValue(String foodName, int regenHearts, int regenHunger, float saturation, List<EffectValue> effects) {
		setName(foodName);
		setRegenHearts(regenHearts);
		setRegenHunger(regenHunger);
		setSaturation(saturation);
		setEffects(effects);
	}
	
	
	private void setName(String foodName) {
		this.name = foodName.toUpperCase();
	}
	
	/**
	 * Get the name of the food
	 * @return foodName
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Get the food as a material
	 * @return material
	 */
	public Material getFood() {
		return Material.valueOf((name.equalsIgnoreCase("enchanted_golden_apple")) ? "GOLDEN_APPLE" : name);
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
	
	public String toString() {
		return name + ", " + regenHearts + " hearts, " + regenHunger + " hunger, " + saturation + " saturation.";
	}
}
