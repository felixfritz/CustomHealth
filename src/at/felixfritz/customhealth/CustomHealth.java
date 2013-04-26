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
	
	@Override
	public void onEnable() {
		
		/*
		 * Save all the configuration in plugins/CustomHealth/config.yml
		 */
		this.saveDefaultConfig();
		this.getConfig().options().copyDefaults(true);
		// Include the effects.txt file
		this.saveResource("effects.txt", true);
		
		/*
		 * CommandExecutor, currently only one command
		 */
		this.getCommand("chealth").setExecutor(new CustomCommand(this));
		
		/*
		 * Make everything ready in the FoodDataBase. It just creates
		 * an ArrayList of every edible food there
		 */
		new FoodDataBase(this.getConfig());
		
		/*
		 * Register the eating event, called as soon as someone eats something, e.g. a fish, carrot or hamburger
		 */
		this.getServer().getPluginManager().registerEvents(new EatingEvent(this.getConfig()), this);
		
		/*
		 * Check, if the two events about changing the food level and changing the health level
		 * should be enabled or not.
		 */
		if(!this.getConfig().getBoolean("settings.change-food-level"))
			this.getServer().getPluginManager().registerEvents(new FoodEvent(), this);
		
		if(!this.getConfig().getBoolean("settings.regain-health"))
			this.getServer().getPluginManager().registerEvents(new HealthEvent(), this);
		
		log.info("[" + this.getDescription().getName() + "] v" + this.getDescription().getVersion() + " is ready.");
	}
	
	@Override
	public void onDisable() {
		log.info("Closed.");
	}
}
