package at.felixfritz.customhealth;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import at.felixfritz.customhealth.command.CustomCommand;
import at.felixfritz.customhealth.event.EatingEvent;
import at.felixfritz.customhealth.event.FoodEvent;
import at.felixfritz.customhealth.event.HealthEvent;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;

public class CustomHealth extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private static CustomHealth plugin;
	
	@Override
	public void onEnable() {
		
		plugin = this;
		
		/*
		 * Save all the configuration in plugins/CustomHealth/config.yml
		 */
		plugin.saveDefaultConfig();
		plugin.getConfig().options().copyDefaults(true);
		// Include the effects.txt file
		plugin.saveResource("effects.txt", true);
		
		/*
		 * CommandExecutor, currently only one command
		 */
		plugin.getCommand("chealth").setExecutor(new CustomCommand());
		
		/*
		 * Make everything ready in the FoodDataBase. It just creates
		 * an ArrayList of every edible food there
		 */
		new FoodDataBase(this.getConfig());
		
		/*
		 * Register the eating event, called as soon as someone eats something, e.g. a fish, carrot or hamburger
		 */
		plugin.getServer().getPluginManager().registerEvents(new EatingEvent(), plugin);
		
		/*
		 * Check, if the two events about changing the food level and changing the health level
		 * should be enabled or not.
		 */
		if(!plugin.getConfig().getBoolean("settings.change-food-level"))
			plugin.getServer().getPluginManager().registerEvents(new FoodEvent(), plugin);
		
		if(!plugin.getConfig().getBoolean("settings.regain-health"))
			plugin.getServer().getPluginManager().registerEvents(new HealthEvent(), plugin);
		
		
		log.info("[" + plugin.getDescription().getName() + "] v" + plugin.getDescription().getVersion() + " is ready.");
	}
	
	@Override
	public void onDisable() {
		plugin.saveDefaultConfig();
		log.info("Closed.");
	}
	
	public static CustomHealth getPlugin() {
		return plugin;
	}
	
	public static void reloadPlugin() {
		plugin.saveConfig();
		plugin.reloadConfig();
		
	}
}
