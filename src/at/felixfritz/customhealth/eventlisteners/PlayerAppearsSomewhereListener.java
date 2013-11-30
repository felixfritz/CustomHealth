package at.felixfritz.customhealth.eventlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import at.felixfritz.customhealth.Database;

public class PlayerAppearsSomewhereListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		playerAppearsAgainEvent(evt);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent evt) {
		playerAppearsAgainEvent(evt);
	}
	
	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent evt) {
		playerAppearsAgainEvent(evt);
	}
	
	private void playerAppearsAgainEvent(PlayerEvent evt) {
		Player p = evt.getPlayer();
		int x = Database.getMaxFoodLevel(p.getWorld());
		
		if(x > 0 && x <= 20)
			p.setFoodLevel(x);
		
		x = Database.getMaxHeartLevel(p.getWorld());
		
		if(x > 0)
			p.setMaxHealth(x);
	}
	
}
