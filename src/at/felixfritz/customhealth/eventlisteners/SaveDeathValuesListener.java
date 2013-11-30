package at.felixfritz.customhealth.eventlisteners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import at.felixfritz.customhealth.Database;

public class SaveDeathValuesListener implements Listener {
	
	private Map<String, PlayerDeathValues> deathValues;
	private boolean keepLevel;
	
	public SaveDeathValuesListener(boolean keepValues, boolean keepLevel) {
		if(keepValues)
			this.deathValues = new HashMap<String, PlayerDeathValues>();
		this.keepLevel = keepLevel;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		evt.setKeepLevel(keepLevel);
		
		if(deathValues != null) {
			Player p = evt.getEntity();
			PlayerDeathValues values = new PlayerDeathValues();
			values.foodLevel = p.getFoodLevel();
			values.saturation = p.getSaturation();
			values.exhaustion = p.getExhaustion();
			
			deathValues.put(p.getName(), values);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerRespawnEvent(PlayerRespawnEvent evt) {
		if(deathValues == null)
			return;
		
		if(!deathValues.containsKey(evt.getPlayer().getName()))
			return;
		
		Player p = evt.getPlayer();
		PlayerDeathValues values = deathValues.get(p.getName());
		p.setSaturation(values.saturation);
		p.setExhaustion(values.exhaustion);
		
		int x = Database.getMaxFoodLevel(evt.getRespawnLocation().getWorld());
		if(x > 0 && values.foodLevel > x)
			p.setFoodLevel(x);
		else
			p.setFoodLevel(values.foodLevel);
	}
	
	private static class PlayerDeathValues {
		private int foodLevel;
		private float saturation;
		private float exhaustion;
	}
	
}
