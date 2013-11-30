package at.felixfritz.customhealth.foodtypes.effects;

import org.bukkit.entity.Player;

import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.util.RandomValue;

/**
 * Set player on fire for a certain amount of time.
 * @author felixfritz
 * @since 0.6
 * @version 0.7
 */
public class EffectBurn extends EffectValue {
	
	private RandomValue duration;
	
	public EffectBurn(String[] parameters) {
		super("burn", parameters);
		
		duration = new RandomValue(30, 30);
		
		if(parameters == null)
			return;
		
		if(!parameters[0].replaceAll(" ", "").isEmpty()) {
			if(RandomValue.parseRandomValue(parameters[0]) != null)
				duration = RandomValue.parseRandomValue(parameters[0]);
			else
				System.err.println("Could not convert the duration for the burning effect into something useful (MIN,MAX : eg. -3,4)");
		}
		if(parameters.length > 1) {
			try {
				this.setProbability(Integer.parseInt(parameters[1].replaceAll(" ", "").replaceAll("%", "")));
			} catch(NumberFormatException e) {
				System.err.println("Could not convert percentage " + parameters[1] + "! Setting it to 100%.");
			}
		}
	}
	
	@Override
	public void applyEffect(Player p) {
		p.setFireTicks(duration.getRandomIntValue() * 20);
	}
	
}
