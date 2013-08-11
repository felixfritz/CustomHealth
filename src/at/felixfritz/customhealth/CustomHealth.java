package at.felixfritz.customhealth;

import java.io.File;
import java.util.ArrayList;
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
	private static YamlConfiguration template;
	
	
	/**
	 * Start the plugin
	 */
	@Override
	public void onEnable() {
		
		plugin = this;
		resourcePath = "plugins/" + getDescription().getName() + "/";
		if(!new File(resourcePath + "worlds/").exists())
			new File(resourcePath + "worlds/").mkdir();
		template = YamlConfiguration.loadConfiguration(getResource("template0x0159.yml"));
		
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
		heartChanger = new ArrayList<World>();
		loadWorldConfig();
		
		getServer().getPluginManager().registerEvents(new FoodEvent(), plugin);
		getServer().getPluginManager().registerEvents(new HealthEvent(), plugin);
		
		
		log.info("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " is ready.");
	}
	
	private static void loadWorldConfig() {
		YamlConfiguration config;
		if(new File(resourcePath + "worlds/").listFiles() == null) {
			System.out.println("Could not find any world config files. Create them with /ch create");
			return;
		}
		for(File file : new File(resourcePath + "worlds/").listFiles()) {
			if(file.getName().endsWith(".yml")) {
				
				config = YamlConfiguration.loadConfiguration(file);
				if(config.getString("worlds") == null)
					continue;
				
				boolean foodChange = config.contains("settings.change-food-level") ? config.getBoolean("settings.change-food-level") : true;
				boolean heartChange = config.contains("settings.regain-health") ? config.getBoolean("settings.regain-health") : true;
				
				for(String worldString : config.getString("worlds").replaceAll(" ", "").split(",")) {
					World world = Bukkit.getWorld(worldString);
					if(world == null)
						continue;
					
					if(FoodDataBase.foods.containsKey(world))
						continue;
					
					if(!foodChange) {
						int max = (config.contains("settings.max-food-level")) ? config.getInt("settings.max-food-level") : 19;
						foodChanger.put(world, (max < 0 || max > 20) ? 19 : max);
					}
					
					if(!heartChange)
						heartChanger.add(world);
					
					FoodDataBase.addWorld(world, config);
				}
				
				
			}
		}
	}
	
	
	/**
	 * Stop the plugin
	 */
	@Override
	public void onDisable() {
		plugin = null;
		resourcePath = null;
		errorPrefix = null;
		foodChanger = null;
		heartChanger = null;
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
		new FoodDataBase(plugin.getConfig());
		loadWorldConfig();
	}
	
	
	/**
	 * Is the food level changing?
	 * @return true, if that's the case
	 */
	public static boolean isFoodLevelChanging(World world) {
		return !foodChanger.containsKey(world);
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
		return !heartChanger.contains(world);
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
	
	/**
	 * Get template
	 * @return
	 */
	public static YamlConfiguration getTemplate() {
		return template;
	}
}
