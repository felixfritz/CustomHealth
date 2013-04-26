package at.felixfritz.customhealth.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * This listener is being called when change-food-level is set to false<br>
 * -> Food level won't change unless you're lower on health an you eat something
 * @author felixfritz
 */
public class FoodEvent implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent evt) {
		evt.setCancelled(true);
		Player p = (Player) evt.getEntity();
		
		//If set to 20, the player isn't able to eat anything anymore.
		p.setFoodLevel(19);
		p.setSaturation(10);
	}
	
}
