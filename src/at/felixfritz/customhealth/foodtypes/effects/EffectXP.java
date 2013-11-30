package at.felixfritz.customhealth.foodtypes.effects;

import org.bukkit.entity.Player;

import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.util.RandomValue;

/**
 * Give XP to the player.
 * @author felixfritz
 * @since 0.6
 * @version 0.7
 */
public class EffectXP extends EffectValue {
	
	private RandomValue amountValue;
	
	public EffectXP(String[] parameters) {
		super("xp", parameters);
		
		if(parameters == null)
			throw new IllegalArgumentException("Parameters for xp-level effects can not be null!");
		
		if(parameters.length == 0)
			throw new IllegalArgumentException("Not enough parameters set for xp-levels!");
		
		String[] values = parameters[0].replaceAll(" ", "").split(",");
		
		try {
			if(values.length == 1)
				amountValue = new RandomValue(Integer.valueOf(values[0]), Integer.valueOf(values[0]));
			else
				amountValue = new RandomValue(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("Could not parse argument into a nice integer.");
		}
		
		if(parameters.length > 1) {
			try {
				this.setProbability(Integer.parseInt(parameters[1].replaceAll(" ", "").replaceAll("%", "")));
			} catch(NumberFormatException e) {
				System.err.println("Could not read percentage " + parameters[1] + " for xp-levels effect. Setting it to 100%");
			}
		}
	}
	
	@Override
	public void applyEffect(Player p) {
		p.giveExp(amountValue.getRandomIntValue());
	}
	
}
