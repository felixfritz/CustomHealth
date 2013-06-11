package at.felixfritz.customhealth.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import at.felixfritz.customhealth.CustomHealth;

public class FoodEvent implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent evt) {
		
		Player p = (Player) evt.getEntity();
		
		if(CustomHealth.isFoodLevelChanging(p.getWorld()))
			return;
		
		evt.setCancelled(true);
		
		//If set to 20, the player isn't able to eat anything anymore.
		p.setFoodLevel(CustomHealth.getMaxFoodLevel(p.getWorld()));
		p.setSaturation(10);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerRespawnEvent evt) {
		
		if(!CustomHealth.isFoodLevelChanging(evt.getPlayer().getWorld()))
			evt.getPlayer().setFoodLevel(CustomHealth.getMaxFoodLevel(evt.getPlayer().getWorld()));
		
	}
}
