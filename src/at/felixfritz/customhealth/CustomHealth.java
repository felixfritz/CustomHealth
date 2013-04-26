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
		this.saveDefaultConfig();
		this.getConfig().options().copyDefaults(true);
		this.saveResource("effects.txt", true);
		
		this.getCommand("chealth").setExecutor(new CustomCommand(this));
		new FoodDataBase(this.getConfig());
		this.getServer().getPluginManager().registerEvents(new EatingEvent(this.getConfig()), this);
		
		if(!this.getConfig().getBoolean("settings.change-food-level"))
			this.getServer().getPluginManager().registerEvents(new FoodEvent(), this);
		
		if(!this.getConfig().getBoolean("settings.regain-health"))
			this.getServer().getPluginManager().registerEvents(new HealthEvent(), this);
		
		log.info("[" + this.getDescription().getName() + "] v" + this.getDescription().getVersion() + " is up.");
	}
	
	@Override
	public void onDisable() {
		log.info("Closed.");
	}
}
