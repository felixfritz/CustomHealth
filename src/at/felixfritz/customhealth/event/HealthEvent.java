package at.felixfritz.customhealth.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

/**
 * This listener is being called when regain-health is set to false<br>
 * -> Heart level won't change if you're saturated, only works with enchantments, potions, etc.
 * @author felixfritz
 */
public class HealthEvent implements Listener {
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onRegainHealth(EntityRegainHealthEvent evt) {
		
		//just check, whether or not the Entity is a player and the player is satiated, which is why he didn't regain hearts
		if(evt.getRegainReason().equals(RegainReason.SATIATED) && (evt.getEntity() instanceof Player)) {
			evt.setCancelled(true);
		}
	}
}
