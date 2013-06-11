package at.felixfritz.customhealth.foodtypes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import at.felixfritz.customhealth.util.FloatValue;
import at.felixfritz.customhealth.util.IntValue;

/**
 * The FoodValue: An object, that contains the material, 
 * the amount of hearts it regenerates and the amount of hunger bars it fills up
 * @author felixfritz
 *
 */
public class FoodValue {
	
	private String name;
	private IntValue regenHearts = new IntValue();
	private IntValue regenHunger = new IntValue();
	private FloatValue saturation = new FloatValue();
	private List<EffectValue> effects = new ArrayList<EffectValue>();
	
	/**
	 * Set it to a specific material. It will regenerate 0 hearts and 0 hunger bars!
	 * @param foodName
	 */
	public FoodValue(String foodName) {
		initialize(foodName, regenHearts, regenHunger, saturation, effects);
	}
	
	public FoodValue(String foodName, IntValue regenHearts, IntValue regenHunger) {
		initialize(foodName, regenHearts, regenHunger, saturation, effects);
	}
	
	public FoodValue(String foodName, IntValue regenHearts, IntValue regenHunger, FloatValue saturation) {
		initialize(foodName, regenHearts, regenHunger, saturation, effects);
	}
	
	public FoodValue(String foodName, IntValue regenHearts, IntValue regenHunger, FloatValue saturation, List<EffectValue> effects) {
		initialize(foodName, regenHearts, regenHunger, saturation, effects);
	}
	
	public FoodValue(String foodName, int minHearts, int maxHearts, int minHunger, int maxHunger) {
		initialize(foodName, new IntValue(minHearts, maxHearts), new IntValue(minHunger, maxHunger), saturation, effects);
	}
	
	public FoodValue(String foodName, int minHearts, int maxHearts, int minHunger, int maxHunger, float minSaturation, float maxSaturation) {
		initialize(foodName, new IntValue(minHearts, maxHearts), new IntValue(minHunger, maxHunger), new FloatValue(minSaturation, maxSaturation), effects);
	}
	
	public FoodValue(String foodName, int minHearts, int maxHearts, int minHunger, int maxHunger, float minSaturation, float maxSaturation, List<EffectValue> effects) {
		initialize(foodName, new IntValue(minHearts, maxHearts), new IntValue(minHunger, maxHunger), new FloatValue(minSaturation, maxSaturation), effects);
	}
	
	
	
	private void initialize(String foodName, IntValue regenHearts, IntValue regenHunger, FloatValue saturation, List<EffectValue> effects) {
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
	
	
	
	public IntValue getRegenHearts() {
		return regenHearts;
	}
	
	
	public void setRegenHearts(IntValue regenHearts) {
		this.regenHearts = regenHearts;
	}
	
	
	public IntValue getRegenHunger() {
		return regenHunger;
	}
	
	
	public void setRegenHunger(IntValue regenHunger) {
		this.regenHunger = regenHunger;
	}
	
	
	public FloatValue getSaturation() {
		return saturation;
	}
	
	
	public void setSaturation(FloatValue saturation) {
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
