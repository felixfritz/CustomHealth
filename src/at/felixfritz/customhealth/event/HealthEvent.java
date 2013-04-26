package at.felixfritz.customhealth.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class HealthEvent implements Listener {
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onRegainHealth(EntityRegainHealthEvent evt) {
		
		if(evt.getRegainReason().equals(RegainReason.SATIATED) && (evt.getEntity() instanceof Player)) {
			evt.setCancelled(true);
		}
	}
}
