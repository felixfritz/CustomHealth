package at.felixfritz.customhealth;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import at.felixfritz.customhealth.command.CommandMain;
import at.felixfritz.customhealth.event.EatingEvent;
import at.felixfritz.customhealth.event.FoodEvent;
import at.felixfritz.customhealth.event.HealthEvent;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;

public class CustomHealth extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	private static CustomHealth plugin;
	private static String resourcePath;
	private static String errorPrefix = "[CustomHealth] [ERROR] ";
	
	private static Map<World, Integer> foodChanger;
	private static List<World> heartChanger;
	
	
	/**
	 * Start the plugin
	 */
	@Override
	public void onEnable() {
		
		plugin = this;
		resourcePath = "plugins/" + getDescription().getName() + "/";
		
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
		foodChanger = new HashMap<World, Integer>();
		loadWorldConfig();
		
		getServer().getPluginManager().registerEvents(new FoodEvent(), plugin);
		getServer().getPluginManager().registerEvents(new HealthEvent(), plugin);
		
		
		log.info("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " is ready.");
	}
	
	private static void loadWorldConfig() {
		YamlConfiguration config;
		for(World world : Bukkit.getServer().getWorlds()) {
			config = YamlConfiguration.loadConfiguration(new File(resourcePath + "worlds/" + world.getName() + ".yml"));
			if(!config.getBoolean("settings.change-food-level"))
				foodChanger.put(world, config.getInt("settings.max-food-level"));
			
			if(!config.getBoolean("settings.regain-health"))
				heartChanger.add(world);
		}
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
		plugin.reloadConfig();
		loadWorldConfig();
	}
	
	
	/**
	 * Is the food level changing?
	 * @return true, if that's the case
	 */
	public static boolean isFoodLevelChanging(World world) {
		return foodChanger.containsKey(world);
	}
	
	/**
	 * Get the max food level
	 * @return maxFoodLevel
	 */
	public static int getMaxFoodLevel(World world) {
		return (isFoodLevelChanging(world)) ? foodChanger.get(world) : -1;
	}
	
	
	/**
	 * Check, if the heart level should be changing or not
	 * @param world
	 * @return
	 */
	public static boolean isHeartLevelChanging(World world) {
		return heartChanger.contains(world);
	}
	
	
	/**
	 * Display an error message to the console
	 * @param message
	 */
	public static void displayErrorMessage(String message) {
		System.err.println(errorPrefix + message);
	}
	
	
	/**
	 * Get the resource path "plugins/CustomHealth/"
	 * @return
	 */
	public static String getResourcePath() {
		return resourcePath;
	}
	
}
