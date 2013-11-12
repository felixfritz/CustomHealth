package at.felixfritz.customhealth.eventlisteners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class HeartChanger implements Listener {
	
	private List<String> worlds;
	
	public HeartChanger(List<String> worlds) {
		this.worlds = worlds;
	}
	
	@EventHandler
	public void onHealthChange(EntityRegainHealthEvent evt) {
		if(evt.getEntity() instanceof Player) {
			if(worlds.contains(evt.getEntity().getWorld()) && evt.getRegainReason().equals(RegainReason.SATIATED)) {
				evt.setCancelled(true);
			}
		}
	}
}
