package at.felixfritz.customhealth.foodtypes.effects;

import org.bukkit.entity.Player;

import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.util.RandomValue;

/**
 * Create an explosion beneath the feet of the player.
 * @author felixfritz
 * @since 0.7
 * @version 0.7
 */
public class EffectExplosion extends EffectValue {
	
	private RandomValue size;
	
	public EffectExplosion(String[] parameters) {
		super("explosion", parameters);
		
		if(parameters.length > 0) {
			if((size = RandomValue.parseRandomValue(parameters[0])) == null) {
				System.err.println("[CustomHealth] " + parameters[0] + " could not be converted to a valid number for an explosion size. Setting it to 1.");
				size = new RandomValue(1);
			}
			
			if(parameters.length > 1) {
				try {
					setProbability(Integer.parseInt(parameters[1].replace("%", "")));
				} catch(NumberFormatException e) {
					System.err.println("[CustomHealth] " + parameters[1] + " could neet be converted into a valid percentage number. Setting it to 100%.");
				}
			}
		}
	}

	@Override
	public void applyEffect(Player p) {
		p.getWorld().createExplosion(p.getLocation(), (float) size.getRandomValue());
	}

}
