package at.felixfritz.customhealth.foodtypes;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import at.felixfritz.customhealth.util.RandomValue;

public class PotionValue {
	
	private final PotionEffectType potion;
	private RandomValue strength;
	private RandomValue duration;
	private float probability;
	
	public PotionValue(PotionEffectType potion) {
		this(potion, new RandomValue(1, 1), new RandomValue(30, 30));
	}
	
	public PotionValue(PotionEffectType potion, RandomValue strength, RandomValue duration) {
		if(potion == null || strength == null || duration == null)
			throw new NullPointerException("PotionEffectType, Strength and Duration cannot be null!");
		
		this.potion = potion;
		this.strength = strength;
		this.duration = duration;
		this.probability = 1f;
	}
	
	public RandomValue getStrength() {
		return strength;
	}
	
	public void setStrength(RandomValue strength) {
		if(strength != null)
			this.strength = strength;
	}
	
	public RandomValue getDuration() {
		return duration;
	}
	
	public void setDuration(RandomValue duration) {
		if(duration != null)
			this.duration = duration;
	}
	
	public PotionEffectType getPotion() {
		return potion;
	}
	
	public void setProbability(float probability) {
		if(probability >= 0 && probability <= 100)
			this.probability = probability;
	}
	
	public void setProbability(int percentage) {
		setProbability(((float) percentage) / 100f);
	}
	
	public float getProbability() {
		return probability;
	}
	
	public PotionEffect getPotionEffect() {
		PotionEffect effect = potion.createEffect(duration.getRandomIntValue() * 80, strength.getRandomIntValue());
		return effect;
	}
	
}
