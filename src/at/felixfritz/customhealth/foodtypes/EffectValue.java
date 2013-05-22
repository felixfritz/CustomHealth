package at.felixfritz.customhealth.foodtypes;

import org.bukkit.potion.PotionEffectType;

public class EffectValue {
	
	private PotionEffectType effect;
	private double probability;
	private int duration;
	private int strength;
	
	public EffectValue(PotionEffectType effect) {
		new EffectValue(effect, 1D, 30, 0);
	}
	
	public EffectValue(PotionEffectType effect, double probability) {
		new EffectValue(effect, probability, 30, 0);
	}
	
	public EffectValue(PotionEffectType effect, double probability, int duration) {
		new EffectValue(effect, probability, duration, 0);
	}
	
	public EffectValue(PotionEffectType effect, double probability, int duration, int strength) {
		this.effect = effect;
		
		this.probability = probability;
		
		if(duration >= 0)
			this.duration = duration;
		else
			this.duration = 30;
		
		if(strength >= 0)
			this.strength = strength;
		else
			this.strength = 0;
	}
	
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public void setDuration(int duration) {
		if(duration >= 0)
			this.duration = duration;
		else
			this.duration = 30;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setStrength(int strength) {
		if(strength >= 0)
			this.strength = strength;
		else
			this.strength = 0;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public PotionEffectType getEffect() {
		return effect;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof EffectValue) {
			return effect == ((EffectValue) o).effect;
		}
		
		return false;
	}
}
