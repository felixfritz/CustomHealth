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
import org.bukkit.potion.PotionEffectType;

import at.felixfritz.customhealth.foodtypes.FoodDataBase;
import at.felixfritz.customhealth.foodtypes.FoodValue;

public class EatingEvent implements Listener {
	
	private FileConfiguration cfg;
	
	public EatingEvent(FileConfiguration cfg) {
		this.cfg = cfg;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerClick(PlayerItemConsumeEvent evt) {
		
		
		evt.setCancelled(true);
		
		Player p = evt.getPlayer();
		Material m = evt.getItem().getType();
		
		if(!p.getItemInHand().getType().equals(Material.MUSHROOM_SOUP))
			p.setItemInHand(new ItemStack(m, p.getItemInHand().getAmount() - 1));
		else
			p.setItemInHand(new ItemStack(Material.BOWL, 1));
		
		if(m.isEdible()) {
			String[] foods = FoodDataBase.getFoodNames();
			for(int x = 0; x < foods.length; x++) {
				if(foods[x].equalsIgnoreCase(m.name())) {
					
					FoodValue tmp = FoodDataBase.foods.get(x);
					
					int health = p.getHealth() + tmp.getRegenHearts();
					int hunger = p.getFoodLevel() + tmp.getRegenHunger();
					
					if(health > 20) health = 20;
					if(hunger > 20) hunger = 20;
					
					if(health < 0) health = 0;
					if(hunger < 0) hunger = 0;
					
					p.setHealth(health);
					p.setFoodLevel(hunger);
					p.setSaturation(1);
					
					if(cfg.getString("food." + m.name().toLowerCase() + ".effects").equalsIgnoreCase("none"))
						continue;
					
					String[] effects = cfg.getString("food." + m.name().toLowerCase() + ".effects").replaceAll("\\[", "").
							replaceAll("\\]", "").replaceAll(" ", "").split(";");
					
					String effectString = "0";
					float effectProbability = 1;
					int effectDuration = 30;
					int effectStrength = 0;
					
					for(String s : effects) {
						String[] wtf = s.split(",");
						
						switch(wtf.length) {
						case 4:
							try {
								effectStrength = Integer.valueOf(wtf[3]);
							} catch(NumberFormatException e) {
								p.sendMessage(ChatColor.RED + wtf[3] + " is not an integer!");
								continue;
							} catch(Exception e) {
								p.sendMessage(ChatColor.RED + "Something went wrong when using " + wtf[3] + ".");
								e.printStackTrace();
							}
						case 3:
							try {
								effectDuration = Integer.valueOf(wtf[2]);
							} catch(NumberFormatException e) {
								p.sendMessage(ChatColor.RED + wtf[2] + " is not an integer!");
								continue;
							} catch(Exception e) {
								p.sendMessage(ChatColor.RED + "Something went wrong when using " + wtf[2] + ".");
								e.printStackTrace();
							}
						case 2:
							try {
								effectProbability = Float.valueOf("0." + wtf[1].replaceAll("%", ""));
							} catch(NumberFormatException e) {
								p.sendMessage(ChatColor.RED + wtf[1] + " is not an integer!");
								continue;
							} catch(Exception e) {
								p.sendMessage(ChatColor.RED + "Something went wrong when using " + wtf[1] + ".");
								e.printStackTrace();
							}
						case 1:
							effectString = wtf[0];
						}
						
						if(effectString.equalsIgnoreCase("0"))
							continue;
						
						if(Math.random() <= effectProbability) {
							try {
								int effectId = Integer.valueOf(effectString);
								p.addPotionEffect(PotionEffectType.getById(effectId).createEffect(
										(40 * effectDuration) + 19, effectStrength));
							} catch(NumberFormatException e) {
								p.addPotionEffect(PotionEffectType.getByName(effectString).createEffect(
										effectDuration, effectStrength));
							} catch(IllegalArgumentException e) {
								p.sendMessage(ChatColor.RED + effectString + " is not registered!");
							} catch(Exception e) {
								p.sendMessage(ChatColor.RED + "Something went wrong. Check the console!");
								e.printStackTrace();
							}
						}
						
					}
					
					
				}
			}
			return;
		}
	}
}
