package at.felixfritz.customhealth.foodtypes.effects;

import org.bukkit.entity.Player;

import at.felixfritz.customhealth.foodtypes.EffectValue;

public class EffectCmd extends EffectValue {
	
	private String cmd;
	
	public EffectCmd(String[] parameters) {
		super("cmd", parameters);
		
		StringBuilder sb = new StringBuilder();
		for(String s : parameters[0].split(" ")) {
			if(s.isEmpty()) continue;
			sb.append(s).append(" ");
		}
		
		if(cmd.length() > 0)
			cmd = sb.substring(0, sb.length() - 1);
		else
			throw new IllegalArgumentException("The cmd does not have a command to use.");
		
		if(parameters.length > 1) {
			try {
				this.setProbability(Integer.parseInt(parameters[1].replaceAll(" ", "").replaceAll("%", "")));
			} catch(NumberFormatException e) {
				System.err.println("[CustomHealth] Could not read percentage " + parameters[1] + " for xp-levels effect. Setting it to 100%");
			}
		}
	}
	
	@Override
	public void applyEffect(Player p) {
		p.performCommand(cmd);
	}
}
