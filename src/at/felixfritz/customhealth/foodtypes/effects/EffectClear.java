package at.felixfritz.customhealth.foodtypes.effects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import at.felixfritz.customhealth.foodtypes.EffectValue;

public class EffectClear extends EffectValue {

	public EffectClear(String[] parameters) {
		super("c", parameters);
		
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
