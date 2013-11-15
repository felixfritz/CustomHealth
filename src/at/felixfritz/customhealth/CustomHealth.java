package at.felixfritz.customhealth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.xml.sax.SAXException;

import at.felixfritz.customhealth.commands.CommandMain;
import at.felixfritz.customhealth.eventlisteners.FoodChanger;
import at.felixfritz.customhealth.eventlisteners.HeartChanger;
import at.felixfritz.customhealth.eventlisteners.MainListener;
import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.foodtypes.FoodValue;
import at.felixfritz.customhealth.foodtypes.PotionValue;
import at.felixfritz.customhealth.foodtypes.effects.EffectBurn;
import at.felixfritz.customhealth.foodtypes.effects.EffectClear;
import at.felixfritz.customhealth.foodtypes.effects.EffectCmd;
import at.felixfritz.customhealth.foodtypes.effects.EffectXP;
import at.felixfritz.customhealth.util.RandomValue;
import at.felixfritz.customhealth.util.Updater;
import at.felixfritz.customhealth.util.Updater.UpdateResult;

/**
 * Main CustomHealth class. Very very very important.
 * @author felixfritz
 * @version 0.6
 */
public class CustomHealth extends JavaPlugin {
	
	private Logger log = Logger.getLogger("Minecraft");
	
	private String prefix = "[CustomHealth] ";
	private String resourcePath;
	
	private static CustomHealth plugin;
	
	/**
	 * onEnable method
	 */
	public void onEnable() {
		
		log.info(prefix + "Loaading up CustomHealth v" + getDescription().getVersion() + ".");
		
		//Save effects.txt in CustomHealth directory. Overwrite all existent files
		saveResource("effects.txt", true);
		
		//Load up configurations
		reloadConfig();
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		
		//Create instance of resource path and make sure, the directory "plugins/CustomHealth/worlds/" exists
		resourcePath = "plugins/" + getDescription().getName() + "/";
		if(!new File(resourcePath + "worlds/").exists())
			new File(resourcePath + "worlds/").mkdir();
		
		//Initialize worlds
		initializeWorlds();
		//Listen for the command "/chealth"
		getCommand("chealth").setExecutor(new CommandMain());
		
		//Save static plugin instance. For all of you against static referencing, screw you, I know what I'm doing. I'm a professional. Especially with commenting. I know when to comment and how long comments should be. They should not be longer than necessary, but long enough to understand the context. Understood? Good.
		plugin = this;
	}
	
	
	/**
	 * Load up all the world settings and save them in the database.
	 * This, at first glance, looks like a big chunk of messy code, but trust me...
	 * well, it is actually a big chunk of messy code.
	 * 
	 * But on the bright side, putting all of the load-up in one method is great,
	 * because you can collapse it all in eclipse and don't have to look at it again!
	 */
	@SuppressWarnings("deprecation")
	private void initializeWorlds() {
		
		Database.initialize();
		
		//Get all available files in "plugin/CustomHealth/worlds/".
		List<File> list = new ArrayList<File>();
		for(File file : new File(resourcePath + "worlds/").listFiles()) {
			if(file.getName().endsWith(".yml")) list.add(file);
		}
		
		//Create instance of YamlConfiguration. This is going to be used in the for-loop.
		YamlConfiguration config;
		String heartRegenPath = "settings.regain-health";		//Save path of regain-health as an instance.
		String foodChangePath = "settings.change-food-level";	//Save path of change-food-level as an instance.
		String maxFoodLevelPath = "settings.max-food-level";	//Save path of max-food-level as an instance.
		
		List<String> heartRegenList = null;
		List<String> foodChangeList = null;
		//Check, if there are any files in "plugin/CustomHealth/worlds/".
		if(list.size() == 0) {
			//No, there are no files. Try and create a template for the user.
			log.info(prefix + "No files found. Creating a template for you to edit.");
			saveResource("template0x0159.yml", true);
			new File("plugins/CustomHealth/template0x0159.yml").renameTo(new File("plugins/CustomHealth/worlds/groupA.yml"));
			log.info(prefix + "Done.");
			return;
		}
		
		//Go through each configuration file and save all the settings in the Database.
		for(int x = 0; x < list.size(); x++) {
			log.info(prefix + Math.round(((double) x) / ((double) list.size()) * 100D) + "% done.");
			
			File file = list.get(x);
			
			//Initialize config instance.
			config = YamlConfiguration.loadConfiguration(file);
			
			//Check, if the config file contains a path called "worlds".
			//If not, it wouldn't make sense to go through the whole loop, because nothing will be saved anyway.
			if(!config.contains("worlds")) {
				log.info(prefix + "Couldn't find any worlds for " + file.getName() + ". Ignoring file.");
				continue;
			}
			
			//Create a generic String-list that contains all the names of the worlds.
			List<String> worlds = new ArrayList<String>();
			
			//All worlds are split with a comma (,). Split them up and check, if the worlds are available (worldName != null).
			for(String s : config.getString("worlds").replaceAll(", ", ",").split(",")) {
				if(getServer().getWorld(s) != null) {
					if(Database.hasWorld(s))
						log.info(prefix + "Duplicate: The world " + s + " in " + file.getName() + " already exists in another file. Ignoring it in here.");
					else
						worlds.add(s);
				} else
					log.info(prefix + s + " in file " + file.getName() + " is not a valid world. Ignoring it.");
			}
			
			//If it couldn't find any worlds that are valid, it will ignore the file.
			if(worlds.size() == 0) {
				log.info(prefix + "No valid worlds found in file " + file.getName() + ". Ignoring file.");
				continue;
			}
			
			//Check for the "regain-health" setting.
			if(!config.contains(heartRegenPath))
				log.info(prefix + "Missing setting \"" + heartRegenPath + "\" in file " + file.getName() + ". Ignoring it.");
			else {
				//I'm checking for the String here, if it is set to "false" or "no".
				//If I would get the boolean right out of the config, it would always return false, if there wasn't a valid entry, which I want to prevent.
				if(config.getString(heartRegenPath).equalsIgnoreCase("false") || config.getString(heartRegenPath).equalsIgnoreCase("no")) {
					if(heartRegenList == null) heartRegenList = new ArrayList<String>();
					for(String s : worlds)
						heartRegenList.add(s);
				}
			}
			
			//Check for the "change-food-level" setting.
			if(!config.contains(foodChangePath))
				log.info(prefix + "Missing setting \"" + foodChangePath + "\" in file " + file.getName() + ". Ignoring it.");
			else {
				//Same as above. Default must be true, if there is no valid entry in the setting, it would return false.
				if(config.getString(foodChangePath).equalsIgnoreCase("false") || config.getString(foodChangePath).equalsIgnoreCase("no")) {
					if(foodChangeList == null) foodChangeList = new ArrayList<String>();
					for(String s : worlds)
						foodChangeList.add(s);
				}
			}
			
			//Check for the "max-food-level" setting.
			if(!config.contains(maxFoodLevelPath))
				log.info(prefix + "Missing setting \"" + maxFoodLevelPath + "\" in file " + file.getName() + ". Ignoring it.");
			else {
				//Only add the setting to the database, if it is above or equal to 0. (Invalid entries will return -1)
				if(config.getInt(maxFoodLevelPath) >= 0) {
					for(String s : worlds)
						Database.maxFoodLevel.put(s, config.getInt(maxFoodLevelPath));
				}
			}
			
			
			if(!config.contains("food")) {
				log.info(prefix + "No food section found in file " + file.getName() + ". Ignoring it.");
				continue;
			}
			
			/*
			 * Big round of applause for the "values-read-section". That's going to be a mess.
			 */
			
			List<FoodValue> values = new ArrayList<FoodValue>();
			
			for(String section : config.getConfigurationSection("food").getKeys(false)) {
				FoodValue value;
				{
					String[] split = section.toUpperCase().split("@");
					Material mat;
					
					try {
						mat = Material.getMaterial(Integer.parseInt(split[0]));
					} catch(NumberFormatException e) {
						mat = Material.getMaterial(split[0]);
					}
					
					if(mat == null) {
						log.info(prefix + split[0] + " in the food section in the file " + file.getName() + " is not a valid food name. Skipping it.");
						continue;
					}
					if(!mat.isEdible() && mat != Material.MILK_BUCKET && mat != Material.CAKE_BLOCK) {
						log.info(prefix + mat + " in the food section in the file " + file.getName() +
								" is not edible and not a MILK_BUCKET or a CAKE_BLOCK. Skipping it.");
						continue;
					}
					
					byte dataValue = 0;
					if(split.length > 1) {
						try {
							dataValue = Byte.parseByte(split[1]);
						} catch(NumberFormatException e) {
							log.info(prefix + split[1] + " in the food section for " + mat + " in the file " + file.getName() + 
									" is not a valid number. Setting it to 0.");
							dataValue = 0;
						}
					}
					
					value = new FoodValue(mat, dataValue);
				}
				
				//Check for "hearts"
				if(config.contains("food." + section + ".hearts")) {
					RandomValue rand = RandomValue.parseRandomValue(config.getString("food." + section + ".hearts"));
					
					if(rand == null)
						log.info(prefix + "Check value for the path \"food." + section + ".hearts\" in the file " + file.getName() + " again."
								+ " It couldn't translate the string " + config.getString("food." + section + ".hearts")
								+ " into something useful (MIN,MAX : eg. -4,3). Ignoring it.");
					else
						value.setRegenHearts(RandomValue.parseRandomValue(config.getString("food." + section + ".hearts")));
				}
				
				//Check for "food"
				if(config.contains("food." + section + ".food")) {
					int i = getHungerRegenValue(value.getMaterial());
					RandomValue rand = RandomValue.parseRandomValue(config.getString("food." + section + ".food"));
					if(rand == null) {
						log.info(prefix + "Check value for the path \"food." + section + ".food\" in the file " + file.getName() + " again."
								+ " It couldn't translate the string " + config.getString("food." + section + ".food")
								+ " into something useful (MIN,MAX : eg. -4,3). Ignoring it.");
					} else {
						rand.setMinValue(rand.getMinValue() - i);
						rand.setMaxValue(rand.getMaxValue() - i);
						value.setRegenHunger(rand);
					}
				}
				
				//Check for "saturation"
				if(config.contains("food." + section + ".saturation")) {
					double i = getSaturationValue(value.getMaterial());
					RandomValue rand = RandomValue.parseRandomValue(config.getString("food." + section + ".saturation"));
					if(rand == null) {
						log.info(prefix + "Check value for the path \"food." + section + ".saturation\" in the file " + file.getName() + " again."
								+ " It couldn't translate the string " + config.getString("food." + section + ".food")
								+ " into something useful (MIN,MAX : eg. -4,3). Ignoring it.");
					} else {
						rand.setMinValue(rand.getMinValue() - i);
						rand.setMaxValue(rand.getMaxValue() - i);
						value.setSaturation(rand);
					}
				}
				
				//Check for "potion" effects
				if(config.contains("food." + section + ".potion")) {
					String[] params;
					PotionValue pv;
					PotionEffectType type;
					for(String potion : config.getString("food." + section + ".potion").replace("[", "").replace("]", "").replaceAll(" ", "").split(";")) {
						params = potion.split("/");
						pv = null;
						type = null;
						
						try {
							type = PotionEffectType.getById(Integer.parseInt(params[0]));
						} catch(NumberFormatException e) {
							type = PotionEffectType.getByName(params[0].toUpperCase());
						}
						
						if(type == null) {
							log.info(prefix + params[0] + " is not a recognizable potion type. Ignoring it. (File: " + file.getName() + ", Path: food." + section + ".potion)");
							continue;
						}
						
						pv = new PotionValue(type);
						
						switch(params.length) {
						case 4:
							try {
								pv.setProbability(Integer.parseInt(params[3].replaceAll("%", "")));
							} catch(NumberFormatException e) {
								log.info(prefix + params[3] + " could not be converted to a valid percentage. Setting probability to 100%. (File: " + file.getName() + ", Path: food." + section + ".potion)");
							}
						
						case 3:
							RandomValue rv = RandomValue.parseRandomValue(params[2]);
							if(rv != null)
								pv.setDuration(rv);
							else
								log.info(prefix + params[2] + " could not be converted to a valid number for the duration (MIN,MAX : eg. 5,30). Setting it to 30 seconds. (File: " + file.getName() + ", Path: food." + section + ".potion)");
						
						case 2:
							RandomValue rv2 = RandomValue.parseRandomValue(params[1]);
							if(rv2 != null)
								pv.setStrength(rv2);
							else
								log.info(prefix + params[1] + " could not be converted to a valid number for the strength (MIN,MAX : eg. 1,4). Setting it to strength 1. (File: " + file.getName() + ", Path: food." + section + ".potion)");
						}
						
						if(value.getPotionEffects().contains(pv))
							log.info(prefix + "Dublicate: " + pv.getPotion() + " exists more than once. Taking first one. (File: " + file.getName() + ", Path: food." + section + ".potion)");
						else
							value.addPotionEffect(pv);
						
					}
				}
				
				//Check for other effects
				if(config.contains("food." + section + ".effect")) {
					for(String effect : config.getString("food." + section + ".effect").replace("[", "").replace("]", "").replaceAll("; ", ";").split(";")) {
						EffectValue ev = null;
						if(effect.toLowerCase().startsWith("xp")) {
							try {
								ev = new EffectXP(effect.replaceAll(" ", "").substring(3).split("/"));
							} catch(IllegalArgumentException e) {
								log.info(prefix + "Illegal arguments for effects in " + section + " in file " + file.getName() + ": " + e.getLocalizedMessage() + ". (" + effect + ") Ignoring it.");
								continue;
							} catch(StringIndexOutOfBoundsException e) {
								log.info(prefix + "xp effects have to have at least 1 argument (xp/<AMOUNT>/<CHANCE>). (" + section + " in file " + file.getName() + ") Ignoring it.");
								continue;
							}
						} else if(effect.toLowerCase().startsWith("clear")) {
							if(effect.replaceAll(" ", "").length() > 5)
								ev = new EffectClear(effect.replaceAll(" ", "").substring(6).split("/"));
							else
								ev = new EffectClear(effect.substring(5).split("/"));
						} else if(effect.toLowerCase().startsWith("burn")) {
							if(effect.replaceAll(" ", "").length() > 4)
								ev = new EffectBurn(effect.replaceAll(" ", "").substring(5).split("/"));
							else
								ev = new EffectBurn(effect.substring(4).split("/"));
						} else if(effect.toLowerCase().startsWith("cmd")) {
							try {
								if(effect.length() > 3)
									ev = new EffectCmd(effect.substring(4).split("/"));
								else
									ev = new EffectCmd(effect.substring(3).split("/"));
							} catch(IllegalArgumentException e) {
								log.info(prefix + "Illegal arguments for effects in " + section + " in file " + file.getName() + " when using \"cmd\": " + e.getLocalizedMessage() + ". (" + effect + ") Ignoring it.");
								continue;
							}
						} else if(effect.toLowerCase().startsWith("cancel")) {
							value.setCancelled(true);
							continue;
						} else if(effect.toLowerCase().startsWith("override")) {
							value.setOverrideEnabled(true);
							continue;
						}
						
						if(ev == null) {
							log.info(prefix + "Could not parse \"" + effect.split("/")[0] + "\" into an effect. Currently there's \"xp\", \"cmd\", \"clear\" and \"burn\". Ignoring it. (File: " + file.getName() + ", Path: food." + section + ".effect)");
							continue;
						}
						
						if(value.getEffects().contains(ev))
							log.info(prefix + "Dublicate: " + ev.getName() + " exists more than once. Taking first one. (File: " + file.getName() + ", Path: food." + section + ".effect)");
						else
							value.addEffect(ev);
					}
				}
				
				values.add(value);
			}
			Database.effects.put(worlds, values);
		}
		
		if(heartRegenList != null)
			Bukkit.getPluginManager().registerEvents(new HeartChanger(heartRegenList), this);
		if(foodChangeList != null)
			Bukkit.getPluginManager().registerEvents(new FoodChanger(foodChangeList), this);
		Bukkit.getPluginManager().registerEvents(new MainListener(), this);
		
		log.info(prefix + "100% done.");
		
	}
	
	
	/**
	 * Unfortunately, I did not find something like an "ItemFood" that would tell me,
	 * how much an apple would regenerate. Therefore this method exists.
	 * @param mat that has to be checked
	 * @return amount of food bars, that are filled up
	 */
	public static int getHungerRegenValue(Material mat) {
		
		if(mat == Material.MILK_BUCKET)
			return 0;
		if(mat == Material.POTATO_ITEM)
			return 1;
		if(mat == Material.CAKE_BLOCK || mat == Material.COOKIE || mat == Material.MELON || mat == Material.POISONOUS_POTATO || mat == Material.RAW_CHICKEN
				|| mat == Material.RAW_FISH || mat == Material.SPIDER_EYE)
			return 2;
		if(mat == Material.PORK || mat == Material.RAW_BEEF)
			return 3;
		if(mat == Material.APPLE || mat == Material.CARROT_ITEM || mat == Material.GOLDEN_APPLE || mat == Material.ROTTEN_FLESH)
			return 4;
		if(mat == Material.BREAD || mat == Material.COOKED_FISH)
			return 5;
		if(mat == Material.BAKED_POTATO || mat == Material.COOKED_CHICKEN || mat == Material.GOLDEN_CARROT || mat == Material.MUSHROOM_SOUP)
			return 6;
		if(mat == Material.COOKED_BEEF || mat == Material.GRILLED_PORK || mat == Material.PUMPKIN_PIE)
			return 8;
		
		CustomHealth.plugin.log.severe(CustomHealth.plugin.prefix + "Could not find any values for " + mat + "! Please report immediately if you think, that's a bug!");
		return 0;
		
	}
	
	
	
	/**
	 * Unfortunately, I did not find something like an "ItemFood" that would tell me,
	 * how much an apple would saturate. Therefore this method exists.
	 * @param mat that has to be checked
	 * @return saturation, that are filled up
	 */
	public static float getSaturationValue(Material mat) {
		
		if(mat == Material.MILK_BUCKET)
			return 0F;
		if(mat == Material.CAKE_BLOCK || mat == Material.COOKIE)
			return 0.4F;
		if(mat == Material.POTATO_ITEM)
			return 0.6F;
		if(mat == Material.ROTTEN_FLESH)
			return 0.8F;
		if(mat == Material.MELON || mat == Material.POISONOUS_POTATO || mat == Material.RAW_CHICKEN || mat == Material.RAW_FISH)
			return 1.2F;
		if(mat == Material.PORK || mat == Material.RAW_BEEF)
			return 1.8F;
		if(mat == Material.APPLE)
			return 2.4F;
		if(mat == Material.SPIDER_EYE)
			return 3.2F;
		if(mat == Material.CARROT_ITEM || mat == Material.PUMPKIN_PIE)
			return 4.8F;
		if(mat == Material.BREAD || mat == Material.COOKED_FISH)
			return 6F;
		if(mat == Material.BAKED_POTATO || mat == Material.COOKED_CHICKEN || mat == Material.MUSHROOM_SOUP)
			return 7.2F;
		if(mat == Material.GOLDEN_APPLE)
			return 9.6F;
		if(mat == Material.COOKED_BEEF || mat == Material.GRILLED_PORK)
			return 12.8F;
		if(mat == Material.GOLDEN_CARROT)
			return 14.4F;
		
		CustomHealth.plugin.log.severe(CustomHealth.plugin.prefix + "Could not find any values for " + mat + "! Please report immediately if you think, that's a bug!");
		return 0;
	}
	
	
	/**
	 * Get resource path of plugin (plugin/CustomHealth/)
	 * @return resourcePath
	 */
	
	public static String getResourcePath() {
		return plugin.resourcePath;
	}
	
	
	/**
	 * Create and save template in "worlds" directory.
	 * @return name of the file (including directory)
	 */
	public static String saveTemplate() {
		//Save file in the CustomHealth-directory. "true" - override file, if existent (which usually isn't)
		plugin.saveResource("template0x0159.yml", true);
		
		/*
		 * To avoid overriding issues in worlds-directory, x will step in and make sure to create a file that doesn't exist yet.
		 * This could potentially break, if the user created 4.294.967.295 yml-files and named them -2.147.483.648, -2.147.483.647 and so on until 2.147.483.647
		 * Good luck with that!
		 */
		int x = 0;
		while(new File("plugins/CustomHealth/worlds/" + x + ".yml").exists())
			x++;
		
		//Create file and return filename
		new File("plugins/CustomHealth/template0x0159.yml").renameTo(new File("plugins/CustomHealth/worlds/" + x + ".yml"));
		return "plugins/CustomHealth/worlds/" + x + ".yml";
	}
	
	
	/**
	 * Reload plugin
	 */
	public static void reload() {
		HandlerList.unregisterAll(plugin);	//Unregister all listeners that the plugin is listening for
		Database.free();					//Free space by setting static objects in Database to null
		plugin.onEnable();					//Now that it's all gone, act as if the plugin would upload freshly
	}
	
	
	/**
	 * Get plugin instance. Tried to avoid this method, but for some scheduling I had to use it (and I didn't want to hand around object instances)
	 * @return CustomHealth
	 */
	public static CustomHealth getPlugin() {
		return plugin;
	}
	
	
	/**
	 * Check, if the update checking feature is enabled
	 * @return true, if it should check for updates
	 */
	public static boolean isUpdateCheckingEnabled() {
		return plugin.getConfig().getBoolean("check-for-updates");
	}
	
	
	/**
	 * Get newest versions (only called, when update checking is enabled)
	 * 
	 * See also the tutorial: http://www.youtube.com/watch?v=vlu6Tk0uPQA
	 * @return string array, containing current version, "online" version and homepage for newest version (if available)
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static String[] getVersions() throws Exception {
		//Create string array, first index is current version, index 2 and 3 will be informations about newer version on dev.bukkit.org, if available
		String[] versions = {plugin.getDescription().getVersion(), null, null};
		
		//Connect to rss-feed of the custom-health project in dev.bukkit.org, can throw IOException
		Updater updater = new Updater(plugin, 56474, plugin.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
		
		if(updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			versions[1] = updater.getLatestName().replaceAll("[a-zA-Z ]", "");
			versions[2] = updater.getLatestFileLink();
		} else if(updater.getResult() != UpdateResult.NO_UPDATE) {
			if(updater.getResult() == UpdateResult.FAIL_DBO)
				throw new Exception("Could not connect to dev.bukkit.org - are you online?");
			if(updater.getResult() == UpdateResult.FAIL_APIKEY)
				throw new Exception("Admin might have improperly configured the API");
			throw new Exception("Unknown reason, check server log");
		}
		
		return versions;
	}
	
	public void onDisable() {
		//Avoid memory leaks and set all static instances to null
		Database.free();
		plugin = null;
		System.gc();
	}
}
