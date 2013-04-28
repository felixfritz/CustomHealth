package at.felixfritz.customhealth.event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import at.felixfritz.customhealth.foodtypes.FoodDataBase;
import at.felixfritz.customhealth.foodtypes.FoodValue;

/**
 * Most important class!<br>
 * Called with the player eating an edible food
 * @author felixfritz
 */
public class EatingEvent implements Listener {
	
	//FileConfiguration, initialized with the constructor
	private FileConfiguration cfg;
	
	/**
	 * Initialize the FileConfiguration above
	 */
	public EatingEvent(FileConfiguration cfg) {
		this.cfg = cfg;
	}
	
	
	/**
	 * Most important method, player ate something!
	 * @param evt
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerConsume(PlayerItemConsumeEvent evt) {
		
		Player p = evt.getPlayer();
		Material m = evt.getItem().getType();
		
		//If the player has an empty glass bottle in his hand, he probably drank a potion -> don't cancel those effects
		if(m.equals(Material.POTION))
			return;
		
		//Don't let the player regain any health or food levels as long as I don't want him to
		evt.setCancelled(true);
		
		/*
		 * Check first, if he ate the MushroomSoup, put a bowl in his inventory then
		 */
		if(!p.getItemInHand().getType().equals(Material.MUSHROOM_SOUP))
			p.setItemInHand(new ItemStack(m, p.getItemInHand().getAmount() - 1));
		else
			p.setItemInHand(new ItemStack(Material.BOWL, 1));
		
		/*
		 * Check, if the player drank milk. Reset all effects, if so.
		 */
		if(p.getItemInHand().getType().equals(Material.MILK_BUCKET)) {
			p.setItemInHand(new ItemStack(Material.BUCKET, 1));
			for(PotionEffect effect : p.getActivePotionEffects())
				p.removePotionEffect(effect.getType());
		}
		
		
		/*
		 * Check first, if the food is edible or not
		 */
		if(m.isEdible()) {
			
			/*
			 * Go through each available food out there and check, which one the player took
			 */
			String[] foods = FoodDataBase.getFoodNames();
			for(int x = 0; x < foods.length; x++) {
				if(foods[x].equalsIgnoreCase(m.name())) {
					
					//Get all the information about the food with the FoodValue object
					FoodValue tmp = FoodDataBase.foods.get(x);
					
					/*
					 * Regain health and hunger.
					 * If it's too much or too less, make sure to set it either to 20 (max) or 0 (min)
					 */
					int health = p.getHealth() + tmp.getRegenHearts();
					int hunger = p.getFoodLevel() + tmp.getRegenHunger();
					
					if(health > 20) health = 20;
					if(hunger > 20) hunger = 20;
					
					if(health < 0) health = 0;
					if(hunger < 0) hunger = 0;
					
					p.setHealth(health);
					p.setFoodLevel(hunger);
					p.setSaturation(10); //Saturation: When it reaches 0, the food bar starts jiggeling
					
					//Check first, if the player has added any effects to the food
					if(cfg.getString("food." + m.name().toLowerCase() + ".effects").equalsIgnoreCase("none"))
						continue;
					
					/*
					 * Remove the [, ] and all spacebars, split it with the ';', add the effects to it
					 */
					addEffects(p, cfg.getString("food." + m.name().toLowerCase() + ".effects").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(";"));
				}
			}
			return;
		}
	}
	
	public void addEffects(Player p, String[] effects) {
		
		String effectString;
		float effectProbability;
		int effectDuration;
		int effectStrength;
		
		for(String effect : effects) {
			effectString = "0";
			effectProbability = 1;
			effectDuration = 30;
			effectStrength = 0;
			
			String[] tmp = effect.split(",");
			switch(tmp.length) {
			case 4:
				try {
					effectStrength = Integer.valueOf(tmp[3]);
				} catch(NumberFormatException e) {
					p.sendMessage(ChatColor.RED + tmp[3] + " is not an integer!");
					continue;
				} catch(Exception e) {
					p.sendMessage(ChatColor.RED + "Something went wrong when using " + tmp[3] + ".");
					e.printStackTrace();
				}
			case 3:
				try {
					effectDuration = Integer.valueOf(tmp[2]);
				} catch(NumberFormatException e) {
					p.sendMessage(ChatColor.RED + tmp[2] + " is not an integer!");
					continue;
				} catch(Exception e) {
					p.sendMessage(ChatColor.RED + "Something went wrong when using " + tmp[2] + ".");
					e.printStackTrace();
				}
			case 2:
				try {
					effectProbability = Float.valueOf(tmp[1].replaceAll("%", "")) / 100;
				} catch(NumberFormatException e) {
					p.sendMessage(ChatColor.RED + tmp[1] + " is not an integer!");
					continue;
				} catch(Exception e) {
					p.sendMessage(ChatColor.RED + "Something went wrong when using " + tmp[1] + ".");
					e.printStackTrace();
				}
			case 1:
				effectString = tmp[0];
			}
			
			if(effectString.equalsIgnoreCase("0"))
				continue;
			
			addEffects(p, effectString, effectProbability, effectDuration, effectStrength);
		}
	}
	
	public void addEffects(Player p, String effectString, float effectProbability, int effectDuration, int effectStrength) {
		//Math.random() is a number between 0 and 1 -> 100% will be always true
		if(Math.random() <= effectProbability) {
			try {
				int effectId = Integer.valueOf(effectString);
				p.removePotionEffect(PotionEffectType.getById(effectId));
				p.addPotionEffect(PotionEffectType.getById(effectId).createEffect(
						(80 * effectDuration) + 19, effectStrength));
			} catch(NumberFormatException e) {
			} catch(IllegalArgumentException e) {
				p.sendMessage(ChatColor.RED + effectString + " is not registered!");
			} catch(Exception e) {
				p.sendMessage(ChatColor.RED + "Something went wrong. Check the console!");
				e.printStackTrace();
			}
		}
	}
}
