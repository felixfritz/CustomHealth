package at.felixfritz.customhealth.foodtypes.effects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import at.felixfritz.customhealth.foodtypes.EffectValue;

/**
 * Clear all existent potion effects (before food item is eaten)
 * @author felixfritz
 * @since 0.6
 * @version 0.7
 */
public class EffectClear extends EffectValue {

	public EffectClear(String[] parameters) {
		super("clear", parameters);
		
		if(parameters == null)
			return;
		
		if(!parameters[0].replaceAll(" ", "").isEmpty()) {
			try {
				this.setProbability(Integer.parseInt(parameters[0].replaceAll("%", "")));
			} catch(NumberFormatException e) {
				System.err.println("Could not convert percentage " + parameters[0] + "! Setting it to 100.");
			}
		}
	}

	@Override
	public void applyEffect(Player p) {
		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
	}
	
	
	
}
