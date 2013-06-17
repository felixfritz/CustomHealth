package at.felixfritz.customhealth.foodtypes;

import org.bukkit.potion.PotionEffectType;

import at.felixfritz.customhealth.util.FloatValue;
import at.felixfritz.customhealth.util.IntValue;


public class EffectValue {
	
	private int effect = -1;
	private FloatValue probability = new FloatValue(1F, 1F);
	private IntValue duration = new IntValue(30, 30);
	private IntValue strength = new IntValue();
	
	public EffectValue() {
		initialize(effect, probability, duration, strength);
	}
	
	public EffectValue(int effect) {
		initialize(effect, probability, duration, strength);
	}
	
	public EffectValue(int effect, FloatValue probability) {
		initialize(effect, probability, duration, strength);
	}
	
	public EffectValue(int effect, FloatValue probability, IntValue duration) {
		initialize(effect, probability, duration, strength);
	}
	
	public EffectValue(int effect, FloatValue probability, IntValue duration, IntValue strength) {
		initialize(effect, probability, duration, strength);
	}
	
	private void initialize(int effect, FloatValue probability, IntValue duration, IntValue strength) {
		setEffect(effect);
		setProbability(probability);
		setDuration(duration);
		setStrength(strength);
	}
	
	public void setEffect(int effect) {
		this.effect = effect;
	}
	
	public void setProbability(FloatValue probability) {
		this.probability = probability;
	}
	
	public FloatValue getProbability() {
		return probability;
	}
	
	public void setDuration(IntValue duration) {
		this.duration.setMin(duration.getMin() >= 0 ? duration.getMin() : 30);
		this.duration.setMax(duration.getMax() >= 0 ? duration.getMax() : 30);
	}
	
	public IntValue getDuration() {
		return duration;
	}
	
	public void setStrength(IntValue strength) {
		strength.decrementAll();
		this.strength = strength;
	}
	
	public IntValue getStrength() {
		return strength;
	}
	
	public int getEffect() {
		return effect;
	}
	
	public boolean hasValidEffect() {
		if(effect == 0)
			return true;
		try {
			PotionEffectType.getById(effect);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public String getName() {
		if(effect == 0)
			return duration.toString() + " XP";
		if(effect < 0)
			return "Remove all effects";
		
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
