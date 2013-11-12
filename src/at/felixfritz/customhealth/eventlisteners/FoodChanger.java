package at.felixfritz.customhealth.eventlisteners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChanger implements Listener {
	
	private List<String> worlds;
	
	public FoodChanger(List<String> worlds) {
		this.worlds = worlds;
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent evt) {
		if(worlds.contains(evt.getEntity().getWorld().getName())) {
			evt.setCancelled(false);
			if(evt.getEntity() instanceof Player)
				((Player) evt.getEntity()).setSaturation(5F);
		}
	}
}
