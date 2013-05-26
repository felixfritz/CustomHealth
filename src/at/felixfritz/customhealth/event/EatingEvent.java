package at.felixfritz.customhealth.event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;
import at.felixfritz.customhealth.foodtypes.FoodValue;

/**
 * Most important class!<br>
 * Called with the player eating an edible food
 * @author felixfritz
 */
public class EatingEvent implements Listener {
	
	
	/**
	 * Most important method, player ate something!
	 * @param evt
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerConsume(PlayerItemConsumeEvent evt) {
		
		Player p = evt.getPlayer();
		ItemStack i = evt.getItem();
		Material m = i.getType();
		
		//If the player has an empty glass bottle in his hand, he probably drank a potion -> don't cancel those effects
		if(m.equals(Material.POTION))
			return;
		
		//Don't let the player regain any health or food levels as long as I don't want him to
		evt.setCancelled(true);
		
		/*
		 * Check first, if he ate the MushroomSoup, put a bowl in his inventory then
		 */
		if(p.getItemInHand().getType().equals(Material.MUSHROOM_SOUP)) {
			
			p.setItemInHand(new ItemStack(Material.BOWL, 1));
		
		} else if(p.getItemInHand().getType().equals(Material.MILK_BUCKET)) {
			
			p.setItemInHand(new ItemStack(Material.BUCKET, 1));
			for(PotionEffect effect : p.getActivePotionEffects())
				p.removePotionEffect(effect.getType());
		
		} else {
			p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
		}
		
		/*
		 * Check first, if the food is edible or not
		 */
		if(m.isEdible()) {
			
			/*
			 * Check the food, the player has eaten
			 */
			System.out.println(i.getData().getData());
			String foodName = (m == Material.GOLDEN_APPLE && i.getData().getData() == 1) ? "enchanted_golden_apple" : m.name();
			FoodValue value = FoodDataBase.getFoodValue(foodName);
			
			foodEaten(p, value);
			
			return;
		}
	}
	
	
	/**
	 * Cake eat event
	 * @param evt
	 */
	@EventHandler
	public void onClickEvent(PlayerInteractEvent evt) {
		try {
			if(evt.getClickedBlock().getType().equals(Material.CAKE_BLOCK)) {
				
				Player p = evt.getPlayer();
				p.setFoodLevel(getCorrectValue(p.getFoodLevel() - 2));
				
				foodEaten(p, FoodDataBase.getFoodValue("cake_block"));
			}
		} catch(NullPointerException e) {
		} catch(Exception e) {
			evt.getPlayer().sendMessage(ChatColor.RED + "Something went wrong. Check the console!");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Checks the value for hearts and food levels, must be between 0 and 20.
	 * @param val
	 * @return
	 */
	private int getCorrectValue(int val) {
		return (val > 20) ? 20 : (val < 0) ? 0 : val;
	}
	
	
	
	/**
	 * Update health and food bar, add effects if there are any
	 * @param player
	 * @param value FoodValue, which contains the amount of health and food bars restored, as well as the effects
	 */
	private void foodEaten(Player p, FoodValue value) {
		/*
		 * Set hunger and health of player.
		 * Make sure it's also between 0 and 20, because it throws an error if it's above or below
		 */
		int health = value.getRegenHearts() + p.getHealth();
		int food = value.getRegenHunger() + p.getFoodLevel();
		float saturate = value.getSaturation() + p.getSaturation();
		
		p.setHealth(getCorrectValue(health));
		p.setFoodLevel(getCorrectValue(food));
		p.setSaturation(saturate);
		
		//Check, if there are any effects on the food
		if(value.getEffects() != null && value.getEffects().size() != 0) {
			
			//Add all the effects from the array
			for(EffectValue effect : value.getEffects()) {
				
				//Quick check for the probability: if a random number between 0 and 1 is greater than the probability (eg. 0.617 > 0.5), don't do anything
				if(Math.random() <= effect.getProbability()) {
					p.removePotionEffect(effect.getEffect());
					p.addPotionEffect(effect.getEffect().createEffect((effect.getDuration() * 20) + 19, effect.getStrength()));
				}
			}
		}
	}
}
