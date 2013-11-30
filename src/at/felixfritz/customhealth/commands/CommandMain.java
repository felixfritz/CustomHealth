package at.felixfritz.customhealth.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.Database;
import at.felixfritz.customhealth.foodtypes.FoodValue;

/**
 * Main command class... Currently also the only one...
 * 
 * @author felixfritz
 * @since 0.1
 * @version 0.6
 */
public class CommandMain implements CommandExecutor, TabCompleter {
	
	//List of player names, used for "cool-down" when resetting plugin (see commandReset and tmpChat method)
	private List<String> players;
	
	//List of players, who are just about to create food items
	private ArrayList<FoodCreators> foodCreators;
	
	public CommandMain() {
		this.players = new ArrayList<String>();
		this.foodCreators = new ArrayList<FoodCreators>();
	}
	
	/**
	 * Main command listener. You should be all familiar with it, I hope.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//Check, if player has any permission at this point. If not, escape this thing.
		if(!hasAnyPermission(sender)) {
			sender.sendMessage("Unknown command. Type \"help\" for help.");
			return true;
		}
		
		//Check, if player is in the "cool-down" list. If so, go to the tmpChat method and escape this method.
		if(players.contains(sender.getName())) {
			tmpChat(sender, args);
			return true;
		}
		
		if(foodCreators.contains(new FoodCreators(sender.getName(), null))) {
			String arg;
			if(args.length > 0) {
				arg = "";
				for(String s : args)
					arg += " " + s;
				arg = arg.substring(1);
			} else
				arg = "";
			if(!FoodCreator.answerMade((Player) sender, foodCreators.get(foodCreators.indexOf(new FoodCreators(sender.getName(), null))), arg))
				foodCreators.remove(new FoodCreators(sender.getName(), null));
			return true;
		}
		
		//All commands have to be at least 1 argument long
		if(args.length > 0) {
			
			//"create" argument, create new templates. See commandCreate method for more.
			if(args[0].equalsIgnoreCase("create") && sender.hasPermission("customhealth.commands.create")) {
				int amount = 1;
				if(args.length > 1) {
					//More than 1 argument, so the player must've stated, how many files he needed.
					try {
						amount = Integer.parseInt(args[1]);
					} catch(NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not really a number. Plugin will create only 1 copy of the template.");
					}
				}
				
				return commandCreate(sender, amount);
			}
			
			//"rename" argument, see commandRename method for more.
			if(args[0].equalsIgnoreCase("rename") && args.length > 2 && sender.hasPermission("customhealth.commands.rename"))
				return commandRename(sender, args[1], args[2]);
			
			//"reload" argument, see static method reload() in CustomHealth for more.
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("customhealth.commands.reload")) {
				CustomHealth.reload(sender);
				return true;
			}
			
			//"reset" argument, see commandReset method for more.
			if(args[0].equalsIgnoreCase("reset") && sender.hasPermission("customhealth.commands.reset"))
				return commandReset(sender);
			
			//"plugin" argument, see commandPlugin for more.
			if(args[0].equalsIgnoreCase("plugin") && sender.hasPermission("customhealth.commands.plugin"))
				return commandPlugin(sender);
			
			//"set" argument, currently inactive but reserved for later usage.
			if(args[0].equalsIgnoreCase("set") && sender.hasPermission("customhealth.commands.set") && sender instanceof Player) {
				return commandSet((Player) sender, args[1]);
			}
			
			//"info" argument, removed for now. If someone really misses that command, call me now.
			if(args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("get")) {
				sender.sendMessage(ChatColor.RED + "This command didn't seem to fit well into the plugin. If you're missing it though, "
						+ "kindly ask the author to re-implement it again.");
				return true;
			}
		}
		
		//If one of the arguments were successful, it should have escaped it immediately.
		//In this case, either there were no arguments or the arguments given were not that useful, so send a help page.
		sendHelpMenu(sender);
		return true;
	}
	
	/**
	 * Send help menu to CommandSender.
	 * @param sender
	 */
	private void sendHelpMenu(CommandSender sender) {
		String prefix = ChatColor.GRAY + "/ch " + ChatColor.GOLD;
		String suffix = ChatColor.GRAY + ": ";
		
		sender.sendMessage(ChatColor.GRAY + "========[ " + ChatColor.GOLD + "CustomHealth" + ChatColor.GRAY + " ]========");
		if(sender.hasPermission("customhealth.commands.create"))
			sender.sendMessage(prefix + "create (#)" + suffix + "Create 1 or (#) world templates to work with.");
		if(sender.hasPermission("customhealth.commands.set"))
			sender.sendMessage(prefix + "set <#>" + suffix + "Set data value of item - create special items.");
		if(sender.hasPermission("customhealth.commands.rename"))
			sender.sendMessage(prefix + "rename <from> <to>" + suffix + "Rename configuration file");
		if(sender.hasPermission("customhealth.commands.reload"))
			sender.sendMessage(prefix + "reload" + suffix + "Reload the plugin.");
		if(sender.hasPermission("customhealth.commands.reset"))
			sender.sendMessage(prefix + "reset" + suffix + "Reset all world configurations.");
		if(sender.hasPermission("customhealth.commands.plugin"))
			sender.sendMessage(prefix + "plugin" + suffix + "Information about this plugin, check for updates.");
	}
	
	/**
	 * Create method, creates creative templates to create creativity in it. So creative.
	 * 
	 * Saves template from main CustomHealth class.
	 * @see at.felixfritz.customhealth.CustomHealth#saveTemplate()
	 * @param sender
	 * @param amount
	 * @return true. Always. It's just to make things look small and neat.
	 */
	private boolean commandCreate(CommandSender sender, int amount) {
		//Name files "groupA.yml", "groupB.yml" and so on
		char letter = 'A';
		for(int x = 0; x < amount; x++) {
			String fileName = CustomHealth.getResourcePath() + "worlds/group" + letter + ".yml";
			
			//Check, if file with the name "group<letter>.yml" exists. If so, increase letter by 1.
			while(new File(fileName).exists()) {
				letter++;
				if(letter > 'Z') {
					sender.sendMessage(ChatColor.RED + "There are already 26 config files!");
					sender.sendMessage(ChatColor.RED + "Sadly, the alphabet only has 26 letters...");
					sender.sendMessage(ChatColor.RED + "Rename some files using \"/ch rename <from> <to>\" if you need more.");
					return true;
				}
				fileName = CustomHealth.getResourcePath() + "worlds/group" + letter + ".yml";
			}
			
			//Save file
			String path = CustomHealth.saveTemplate();
			new File(path).renameTo(new File(fileName));
			
			//Send message to player, what file (filename) has been created
			sender.sendMessage(ChatColor.GREEN + "Created " + ChatColor.DARK_GREEN + "group" + letter + ".yml " + ChatColor.GREEN + "file.");
		}
		return true;
	}
	
	private boolean commandRename(CommandSender sender, String from, String to) {
		if(!from.endsWith(".yml"))
			from += ".yml";
		
		if(!to.endsWith(".yml"))
			to += ".yml";
		
		from = CustomHealth.getResourcePath() + "worlds/" + from;
		to = CustomHealth.getResourcePath() + "worlds/" + to;
		
		if(new File(to).exists())
			sender.sendMessage(ChatColor.DARK_RED + to + ChatColor.RED + " already exists. Use another name!");
		else if(!new File(from).exists())
			sender.sendMessage(ChatColor.DARK_RED + from + ChatColor.RED + " does not exist.");
		else {
			new File(from).renameTo(new File(to));
			sender.sendMessage(ChatColor.DARK_GREEN + from + ChatColor.GREEN + " renamed to " + ChatColor.DARK_GREEN + to + ChatColor.GREEN + ".");
		}
		
		return true;
	}
	
	/**
	 * Reset the whole configuration. Player is then added to the "cool-down" list for up to 10 seconds to choose, if he really wants to delete all of it.
	 * @param sender
	 * @return true. Always.
	 */
	private boolean commandReset(final CommandSender sender) {
		//Add player to the cool-down list.
		players.add(sender.getName());
		
		//Ask for approval from the player to really delete everything.
		sender.sendMessage(ChatColor.GOLD + "Do you want to delete all configuration files?");
		sender.sendMessage(ChatColor.RED + "\"/ch yes\": Delete all to reset");
		
		//Some special options for the player in-game
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.RED + "\"/ch world\": Only delete group file of the world you're standing in");
		
		sender.sendMessage(ChatColor.GREEN + "\"/ch no\": Cancel procedure");
		
		sender.sendMessage(ChatColor.GOLD + "Process automatically cancelled in 10 seconds.");
		
		//Call tmpChat automatically after 10 seconds, no matter what.
		Bukkit.getScheduler().scheduleSyncDelayedTask(CustomHealth.getPlugin(), new Runnable() {
			public void run() {
				tmpChat(sender, new String[]{"no"});
			}
		}, 10 * 20);
		
		return true;
	}
	
	/**
	 * The tmpChat, only called when player / CommandSender is in the cool-down list
	 * @param sender
	 * @param args
	 */
	private void tmpChat(CommandSender sender, String[] args) {
		//Check, if sender is in that list. If not, just escape method.
		if(!players.contains(sender.getName()))
			return;
		
		//There has to be at least 1 argument
		if(args.length > 0) {
			//"no" - don't delete any of those files
			if(args[0].equalsIgnoreCase("no")) 
				sender.sendMessage(ChatColor.GOLD + "Process cancelled.");
			
			//"world" - delete configuration files that contain the worlds, in which the player stands in
			else if(args[0].equalsIgnoreCase("world") && sender instanceof Player) {
				YamlConfiguration config;
				for(File file : new File(CustomHealth.getResourcePath() + "worlds/").listFiles()) {
					if(!file.getName().endsWith(".yml"))
						continue;
					
					config = YamlConfiguration.loadConfiguration(file);
					if(config.contains("worlds")) {
						for(String s : config.getString("worlds").replaceAll(", ", ",").split(",")) {
							if(((Player) sender).getWorld().getName().equals(s)) {
								if(file.delete())
									sender.sendMessage(ChatColor.DARK_GREEN + file.getName() + ChatColor.GREEN + " successfully deleted.");
								else
									sender.sendMessage(ChatColor.RED + "World seems to be in a file called " + file.getName() + " but couldn't delete it for an unknown reason.");
							}
						}
					}
				}
				
				sender.sendMessage(ChatColor.RED + "There doesn't seem to be a file that contains the world " + ChatColor.DARK_RED + ((Player) sender).getWorld().getName());
			}
			//"yes" - delete all .yml files and create a new template
			else if(args[0].equalsIgnoreCase("yes")) {
				int amount = 0;
				for(File file : new File(CustomHealth.getResourcePath() + "worlds/").listFiles()) {
					if(file.getName().endsWith(".yml")) {
						if(file.delete()) amount++;
					}
				}
				sender.sendMessage(ChatColor.DARK_GREEN + "" + amount + ChatColor.GREEN + " files deleted!");
				new File(CustomHealth.saveTemplate()).renameTo(new File(CustomHealth.getResourcePath() + "worlds/groupA.yml"));
			}
			
			//Remove player from cool-down list
			players.remove(sender.getName());
			return;
		}
		
		/*
		 * Could not interpret arguments, ask again
		 */
		sender.sendMessage(ChatColor.GOLD + "Do you want to delete all configuration files?");
		sender.sendMessage(ChatColor.RED + "\"/ch yes\": Delete all to reset");
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.RED + "\"/ch world\": Only delete group file of the world you're standing in");
		sender.sendMessage(ChatColor.GREEN + "\"/ch no\": Cancel procedure");
		sender.sendMessage(ChatColor.GOLD + "Less than 10 seconds remaining.");
		return;
		
	}
	
	/**
	 * plugin command called. Also checks for updates, if enabled.
	 * @see at.felixfritz.customhealth.CustomHealth#getVersions()
	 * @param sender
	 * @return true. Always.
	 */
	private boolean commandPlugin(CommandSender sender) {
		//Send generic informations, name, version and author.
		sender.sendMessage(ChatColor.LIGHT_PURPLE + CustomHealth.getPlugin().getDescription().getName() + 
				", v" + CustomHealth.getPlugin().getDescription().getVersion() + " by " + CustomHealth.getPlugin().getDescription().getAuthors().get(0));
		
		//Send main BukkitDev homepage information
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "BukkitDev page: " + CustomHealth.getPlugin().getDescription().getWebsite());
		
		//Is update checking enabled?
		if(CustomHealth.isUpdateCheckingEnabled()) {
			//Get string array from CustomHealth class. If index 1 is null, then there's no new update available
			String[] versions = CustomHealth.getVersions();
			if(versions == null)
				sender.sendMessage(ChatColor.GREEN + "Plugin is up to date!");
			else {
				sender.sendMessage(ChatColor.GREEN + "Update available. Current version: " + versions[0] + ", latest version: " + versions[1] + ".");
				sender.sendMessage(ChatColor.GREEN + "Download here: " + versions[2]);
			}
		} else {
			//Update checking is disabled.
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "Update checking disabled.");
		}
		return true;
	}
	
	
	private boolean commandSet(final Player p, String arg) {
		try {
			short damageValue = Short.parseShort(arg);
			p.getItemInHand().setDurability(damageValue);
			p.sendMessage(ChatColor.GREEN + "Set durability for " + p.getItemInHand().getType().name().toLowerCase() + " to " + ChatColor.DARK_GREEN 
					+ damageValue + ChatColor.GREEN + ".");
			
			if(Database.getFoodValue(p.getWorld(), p.getItemInHand()) == null) {
				p.sendMessage(ChatColor.YELLOW + "The item " + ChatColor.GOLD + p.getItemInHand().getType() 
						+ ChatColor.YELLOW + " with the datavalue " + ChatColor.GOLD + damageValue + ChatColor.YELLOW 
						+ " is not in the configuration file");
				p.sendMessage(ChatColor.YELLOW + "Do you want to create one in-game? (10 seconds left)");
				
			} else {
				p.sendMessage(ChatColor.YELLOW + "The item " + ChatColor.GOLD + p.getItemInHand().getType() 
						+ ChatColor.YELLOW + " with the datavalue " + ChatColor.GOLD + damageValue + ChatColor.YELLOW 
						+ " already exists.");
				p.sendMessage(ChatColor.YELLOW + "Do you want to overwrite it in-game? (10 seconds lef)");
			}
			
			p.sendMessage(ChatColor.GREEN + "/ch yes");
			p.sendMessage(ChatColor.RED + "/ch no");
			foodCreators.add(new FoodCreators(p.getName(), new FoodValue(p.getItemInHand())));
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomHealth.getPlugin(), new Runnable() {
				public void run() {
					int x;
					if((x = foodCreators.indexOf(new FoodCreators(p.getName(), null))) >= 0) {
						if(foodCreators.get(x).step == 0) {
							foodCreators.remove(x);
							p.sendMessage(ChatColor.RED + "Process cancelled.");
						}
					}
				}
			}, 20 * 10);
			
		} catch(NumberFormatException e) {
			p.sendMessage(ChatColor.DARK_RED + arg + ChatColor.RED + " is not a whole number that I can understant.");
		}
		return true;
	}
	
	
	/**
	 * Check, if CommandSender has any permission to use any of the commands.
	 * @param sender
	 * @return true, if he's allowed to use at least one of those
	 */
	private boolean hasAnyPermission(CommandSender sender) {
		for(Permission per : CustomHealth.getPlugin().getDescription().getPermissions()) {
			if(sender.hasPermission(per))
				return true;
		}
		return false;
	}
	
	/**
	 * onTabComplete method. This is so useful most of the times, yet so many people don't put it in their plugins. Why?
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		//Put all arguments in lower-case in the first place, otherwise I always have to do it in the if-statements
		for(int x = 0; x < args.length; x++)
			args[x] = args[x].toLowerCase();
		
		if(foodCreators.contains(new FoodCreators(sender.getName(), null)))
			return FoodCreator.onTabComplete(foodCreators.get(foodCreators.indexOf(new FoodCreators(sender.getName(), null))), args);
		
		//List of strings that could be used for the tab-complete feature.
		List<String> list = new ArrayList<String>();
		
		//Arguments 1 long, basic arguments can be added
		if(args.length == 1) {
			
			if("create".startsWith(args[0]) && sender.hasPermission("customhealth.commands.create"))
				list.add("create");
			if("set".startsWith(args[0]) && sender.hasPermission("customhealth.commands.set"))
				list.add("set");
			if("rename".startsWith(args[0]) && sender.hasPermission("customhealth.commands.rename"))
				list.add("rename");
			if("reload".startsWith(args[0]) && sender.hasPermission("customhealth.commands.reload"))
				list.add("reload");
			if("reset".startsWith(args[0]) && sender.hasPermission("customhealth.commands.reset"))
				list.add("reset");
			if("plugin".startsWith(args[0]) && sender.hasPermission("customhealth.commands.plugin"))
				list.add("plugin");
			
		} else if(args.length == 2) {
			//Arguments 2 long, either add filenames for the rename method or numbers for the create method.
			if(args[0].equals("rename") && sender.hasPermission("customhealth.commands.rename")) {
				for(File file : new File(CustomHealth.getResourcePath() + "worlds/").listFiles()) {
					if(file.getName().endsWith(".yml") && file.getName().toLowerCase().startsWith(args[1]))
						list.add(file.getName().substring(0, file.getName().length() - 4));
				}
			}
			
			if((args[0].equals("create") && sender.hasPermission("customhealth.commands.create")) || (args[0].equals("set") && sender.hasPermission("customhealth.commands.set"))) {
				for(int x = 1; x <= 25; x++)
					list.add(String.valueOf(x));
			}
		}
		
		return list;
	}
	
	
	protected static class FoodCreators {
		
		public String name;
		public FoodValue value;
		public int step;
		
		public FoodCreators(String name, FoodValue value) {
			this.name = name;
			this.value = value;
			this.step = 0;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof String)
				return name.equalsIgnoreCase((String) o);
			if(o instanceof FoodCreators)
				return name.equals(((FoodCreators) o).name);
			return false;
		}
	}
}
