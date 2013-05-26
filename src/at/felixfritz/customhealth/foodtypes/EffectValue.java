package at.felixfritz.customhealth.foodtypes;

import org.bukkit.potion.PotionEffectType;


public class EffectValue {
	
	private int effect;
	private double probability;
	private int duration;
	private int strength;
	
	public EffectValue(int effect) {
		new EffectValue(effect, 1D, 30, 0);
	}
	
	public EffectValue(int effect, double probability) {
		new EffectValue(effect, probability, 30, 0);
	}
	
	public EffectValue(int effect, double probability, int duration) {
		new EffectValue(effect, probability, duration, 0);
	}
	
	public EffectValue(int effect, double probability, int duration, int strength) {
		this.effect = effect;
		setProbability(probability);
		setDuration(duration);
		setStrength(strength);
	}
	
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public void setDuration(int duration) {
		this.duration = (duration >= 0) ? duration : 30;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setStrength(int strength) {
		this.strength = (strength >= 0) ? strength : 0;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public int getEffect() {
		return effect;
	}
	
	public String getName() {
		if(effect == 0)
			return duration + " XP";
		
		return PotionEffectType.getById(effect).getName();
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof EffectValue) {
			return effect == ((EffectValue) o).effect;
		}
		
		return false;
	}
}
