package at.felixfritz.customhealth;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import at.felixfritz.customhealth.command.CommandMain;
import at.felixfritz.customhealth.event.EatingEvent;
import at.felixfritz.customhealth.event.FoodEvent;
import at.felixfritz.customhealth.event.HealthEvent;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;

public class CustomHealth extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	private static CustomHealth plugin;
	
	
	/**
	 * Start the plugin
	 */
	@Override
	public void onEnable() {
		
		plugin = this;
		
		/*
		 * Save all the configuration in plugins/CustomHealth/config.yml
		 */
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		// Include the effects.txt file
		saveResource("effects.txt", true);
		
		/*
		 * CommandExecutor, send everything to the main command class
		 */
		getCommand("chealth").setExecutor(new CommandMain());
		
		/*
		 * Make everything ready in the FoodDataBase. It just creates
		 * an ArrayList of every edible food there
		 */
		new FoodDataBase(plugin.getConfig());
		
		/*
		 * Register the eating event, called as soon as someone eats something, e.g. a fish, carrot or hamburger
		 */
		getServer().getPluginManager().registerEvents(new EatingEvent(), plugin);
		
		/*
		 * Check, if the two events about changing the food level and changing the health level
		 * should be enabled or not.
		 */
		if(!getConfig().getBoolean("settings.change-food-level"))
			getServer().getPluginManager().registerEvents(new FoodEvent(), plugin);
		
		if(!getConfig().getBoolean("settings.regain-health"))
			getServer().getPluginManager().registerEvents(new HealthEvent(), plugin);
		
		
		log.info("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " is ready.");
	}
	
	
	/**
	 * Stop the plugin
	 */
	@Override
	public void onDisable() {
		plugin.saveDefaultConfig();
		log.info("[" + getDescription().getName() + "] Closed.");
	}
	
	
	/**
	 * Get the CustomHealth plugin
	 * @return (static) this
	 */
	public static CustomHealth getPlugin() {
		return plugin;
	}
	
	
	/**
	 * Reload the plugin
	 */
	public static void reloadPlugin() {
		plugin.saveConfig();
		plugin.reloadConfig();
		new FoodDataBase(plugin.getConfig());
	}
}
