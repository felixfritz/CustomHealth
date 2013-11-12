package at.felixfritz.customhealth.eventlisteners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.Database;
import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.foodtypes.FoodValue;
import at.felixfritz.customhealth.foodtypes.PotionValue;

public class MainListener implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent evt) {
		FoodValue value = Database.getFoodValue(evt.getPlayer().getWorld(), evt.getItem());
		if(value != null) {
			if(value.isCancelled()) {
				evt.setCancelled(true);
				return;
			}
			
			if(value.isOverrideEnabled()) {
				evt.setCancelled(true);
				evt.getPlayer().getItemInHand().setAmount(evt.getPlayer().getItemInHand().getAmount() - 1);
				evt.getPlayer().setFoodLevel(evt.getPlayer().getFoodLevel() + CustomHealth.getHungerRegenValue(evt.getItem().getType()));
				evt.getPlayer().setSaturation(evt.getPlayer().getSaturation() + CustomHealth.getSaturationValue(evt.getItem().getType()));
				evt.getPlayer().updateInventory();
			}
		}
		
		itemConsumed(evt.getPlayer(), value, evt.getItem().getType());
	}
	
	
	@EventHandler
	public void onClickEvent(PlayerInteractEvent evt) {
		if(evt.getClickedBlock() == null)
			return;
		
		if(evt.getClickedBlock().getType() == Material.CAKE_BLOCK) {
			FoodValue value = Database.getFoodValue(evt.getPlayer().getWorld(), new ItemStack(Material.CAKE_BLOCK));
			
			if(value == null) {
				itemConsumed(evt.getPlayer(), value, Material.CAKE_BLOCK);
				return;
			}
			
			if(value.isCancelled())
				evt.setCancelled(true);
			else
				itemConsumed(evt.getPlayer(), value, Material.CAKE_BLOCK);
		}
	}
	
	
	private void itemConsumed(Player p, FoodValue value, Material item) {
		if(value != null) {
			p.setHealth(getCorrectValue(p.getHealth() + value.getRegenHearts().getRandomValue()));
			p.setFoodLevel((int) getCorrectValue(p.getFoodLevel() + value.getRegenHunger().getRandomIntValue()));
			p.setSaturation((float) (p.getSaturation() + value.getSaturation().getRandomValue()));
			
			if(value.getEffects().contains("c")) {
				if(Math.random() <= value.getEffect("c").getProbability())
				for(PotionEffect effecta : p.getActivePotionEffects())
					p.removePotionEffect(effecta.getType());
				value.getEffects().remove(value.getEffects().indexOf("c"));
			}
			
			for(EffectValue effect : value.getEffects()) {
				if(Math.random() <= effect.getProbability())
					effect.applyEffect(p);
			}
			
			for(PotionValue potion : value.getPotionEffects()) {
				if(Math.random() <= potion.getProbability()) {
					if(p.hasPotionEffect(potion.getPotion()))
						p.removePotionEffect(potion.getPotion());
					p.addPotionEffect(potion.getPotionEffect());
				}
			}
		}
		
		int x = Database.getMaxFoodLevel(p.getWorld());
		if(x >= 0 && p.getFoodLevel() + CustomHealth.getHungerRegenValue(item) > x)
			p.setFoodLevel(x - CustomHealth.getHungerRegenValue(item));
	}
	
	private double getCorrectValue(double value) {
		if(value > 20) return 20;
		if(value < 0) return 0;
		return value;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		Player p = evt.getPlayer();
		int x = Database.getMaxFoodLevel(p.getWorld());
		
		if(x >= 0 && p.getFoodLevel() > x)
			p.setFoodLevel(x);
	}
}
