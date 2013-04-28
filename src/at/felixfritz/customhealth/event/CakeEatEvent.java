package at.felixfritz.customhealth.event;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CakeEatEvent implements Listener {
	
	FileConfiguration cfg;
	
	public CakeEatEvent(FileConfiguration cfg) {
		this.cfg = cfg;
	}
	
	@EventHandler
	public void onClickEvent(PlayerInteractEvent evt) {
		if(evt.getClickedBlock().equals(Material.CAKE)) {
			Player p = evt.getPlayer();
			p.setFoodLevel(p.getFoodLevel() - 2 + cfg.getInt("food.cake_slice.food"));
			p.setHealth(p.getHealth() + cfg.getInt("food.cake_slice.hearts"));
			
		}
	}
	
}
