package at.felixfritz.customhealth.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodEvent implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent evt) {
		Player p = (Player) evt.getEntity();
		p.setHealth(19);
		p.setSaturation(10);
		evt.setCancelled(true);
	}
	
}
