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

public class CommandMain implements CommandExecutor, TabCompleter {
	
	private List<String> players;
	
	public CommandMain() {
		this.players = new ArrayList<String>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!hasAnyPermission(sender)) {
			sender.sendMessage("Unknown command. Type \"help\" for help.");
			return true;
		}
		
		if(players.contains(sender.getName())) {
			tmpChat(sender, args);
			return true;
		}
		
		if(args.length > 0) {
			
			if(args[0].equalsIgnoreCase("create") && sender.hasPermission("customhealth.commands.create")) {
				int amount = 1;
				if(args.length > 1) {
					try {
						amount = Integer.parseInt(args[1]);
					} catch(NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not really a number. Plugin will create only 1 copy of the template.");
					}
				}
				
				return commandCreate(sender, amount);
			}
			
			if(args[0].equalsIgnoreCase("rename") && args.length > 2 && sender.hasPermission("customhealth.commands.rename"))
				return commandRename(sender, args[1], args[2]);
			
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("customhealth.commands.reload")) {
				CustomHealth.reload();
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reset") && sender.hasPermission("customhealth.commands.reset"))
				return commandReset(sender);
			
			if(args[0].equalsIgnoreCase("plugin") && sender.hasPermission("customhealth.commands.plugin"))
				return commandPlugin(sender);
			
			if(args[0].equalsIgnoreCase("set")) {
				sender.sendMessage(ChatColor.RED + "\"set\" command is under construction, don't ya worry!");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("get")) {
				sender.sendMessage(ChatColor.RED + "This command didn't seem to fit well into the plugin. If you're missing it though, "
						+ "kindly ask the author to re-implement it again.");
				return true;
			}
		}
		
		sendHelpMenu(sender);
		return true;
	}
	
	
	private void sendHelpMenu(CommandSender sender) {
		String prefix = ChatColor.GRAY + "/ch " + ChatColor.GOLD;
		String suffix = ChatColor.GRAY + ": ";
		
		sender.sendMessage(ChatColor.GRAY + "========[ " + ChatColor.GOLD + "CustomHealth" + ChatColor.GRAY + " ]========");
		sender.sendMessage(prefix + "create (#)" + suffix + "Create 1 or (#) world templates to work with.");
		sender.sendMessage(prefix + "rename <from> <to>" + suffix + "Rename configuration file");
		sender.sendMessage(prefix + "reload" + suffix + "Reload the plugin.");
		sender.sendMessage(prefix + "reset" + suffix + "Reset all world configurations.");
		sender.sendMessage(prefix + "plugin" + suffix + "Information about this plugin, check for updates.");
	}
	
	private boolean commandCreate(CommandSender sender, int amount) {
		char letter = 'A';
		for(int x = 0; x < amount; x++) {
			String fileName = CustomHealth.getResourcePath() + "worlds/group" + letter + ".yml";
			
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
			
			String path = CustomHealth.saveTemplate();
			new File(path).renameTo(new File(fileName));
			
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
	
	private boolean commandReset(final CommandSender sender) {
		players.add(sender.getName());
		sender.sendMessage(ChatColor.GOLD + "Do you want to delete all configuration files?");
		sender.sendMessage(ChatColor.RED + "\"/ch yes\": Delete all to reset");
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.RED + "\"/ch world\": Only delete group file of the world you're standing in");
		sender.sendMessage(ChatColor.GREEN + "\"/ch no\": Cancel procedure");
		sender.sendMessage(ChatColor.GOLD + "Process automatically cancelled in 10 seconds.");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(CustomHealth.getPlugin(), new Runnable() {
			public void run() {
				tmpChat(sender, new String[]{"no"});
			}
		}, 10 * 20);
		
		return true;
	}
	
	private void tmpChat(CommandSender sender, String[] args) {
		if(!players.contains(sender.getName()))
			return;
		
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("no")) {
				sender.sendMessage(ChatColor.GOLD + "Process cancelled.");
				players.remove(sender.getName());
				return;
			}
			if(args[0].equalsIgnoreCase("world") && sender instanceof Player) {
				YamlConfiguration config;
				for(File file : new File(CustomHealth.getResourcePath() + "worlds/").listFiles()) {
					if(!file.getName().endsWith(".yml"))
						continue;
					
					config = YamlConfiguration.loadConfiguration(file);
					if(config.contains("worlds")) {
						for(String s : config.getString("worlds").replaceAll(", ", ",").split(",")) {
							if(((Player) sender).getWorld().getName().equals(s)) {
								if(file.delete()) {
									sender.sendMessage(ChatColor.DARK_GREEN + file.getName() + ChatColor.GREEN + " successfully deleted.");
									return;
								} else {
									sender.sendMessage(ChatColor.RED + "World seems to be in a file called " + file.getName() + 
											" but couldn't delete it for an unknown reason.");
									return;
								}
							}
						}
					}
				}
				
				sender.sendMessage(ChatColor.RED + "There doesn't seem to be a file that contains the world " + ChatColor.DARK_RED + ((Player) sender).getWorld().getName());
				players.remove(sender.getName());
				return;
			}
			if(args[0].equalsIgnoreCase("yes")) {
				int amount = 0;
				for(File file : new File(CustomHealth.getResourcePath() + "worlds/").listFiles()) {
					if(file.getName().endsWith(".yml")) {
						if(file.delete()) amount++;
					}
				}
				sender.sendMessage(ChatColor.DARK_GREEN + "" + amount + ChatColor.GREEN + " files deleted!");
				new File(CustomHealth.saveTemplate()).renameTo(new File(CustomHealth.getResourcePath() + "worlds/groupA.yml"));
				players.remove(sender.getName());
				return;
			}
		}
		
		sender.sendMessage(ChatColor.GOLD + "Do you want to delete all configuration files?");
		sender.sendMessage(ChatColor.RED + "\"/ch yes\": Delete all to reset");
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.RED + "\"/ch world\": Only delete group file of the world you're standing in");
		sender.sendMessage(ChatColor.GREEN + "\"/ch no\": Cancel procedure");
		sender.sendMessage(ChatColor.GOLD + "Less than 10 seconds remaining.");
		return;
		
	}
	
	private boolean commandPlugin(CommandSender sender) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + CustomHealth.getPlugin().getDescription().getName() + 
				", v" + CustomHealth.getPlugin().getDescription().getVersion() + " by " + CustomHealth.getPlugin().getDescription().getAuthors().get(0));
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "BukkitDev page: " + CustomHealth.getPlugin().getDescription().getWebsite());
		
		if(CustomHealth.isUpdateCheckingEnabled()) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "Checking for updates...");
			
			try {
				String[] versions = CustomHealth.getVersions();
				if(versions[1] == null)
					sender.sendMessage(ChatColor.GREEN + "Plugin is up to date!");
				else {
					sender.sendMessage(ChatColor.GREEN + "Update available. Current version: " + versions[0] + ", latest version: " + versions[1] + ".");
					sender.sendMessage(ChatColor.GREEN + "Download here: " + versions[2]);
				}
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Had difficulties while talking to the server (" + e.getLocalizedMessage() + ")");
			}
		} else {
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "Update checking disabled.");
		}
		return true;
	}
	
	private boolean hasAnyPermission(CommandSender sender) {
		for(Permission per : CustomHealth.getPlugin().getDescription().getPermissions()) {
			if(sender.hasPermission(per))
				return true;
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		for(int x = 0; x < args.length; x++)
			args[x] = args[x].toLowerCase();
		
		if(args.length == 1) {
			
			if("create".startsWith(args[0]) && sender.hasPermission("customhealth.commands.create"))
				list.add("create");
			if("rename".startsWith(args[0]) && sender.hasPermission("customhealth.commands.rename"))
				list.add("rename");
			if("reload".startsWith(args[0]) && sender.hasPermission("customhealth.commands.reload"))
				list.add("reload");
			if("reset".startsWith(args[0]) && sender.hasPermission("customhealth.commands.reset"))
				list.add("reset");
			if("plugin".startsWith(args[0]) && sender.hasPermission("customhealth.commands.plugin"))
				list.add("plugin");
			
		} else if(args.length == 2) {
			if(args[0].equals("rename") && sender.hasPermission("customhealth.commands.rename")) {
				for(File file : new File(CustomHealth.getResourcePath() + "worlds/").listFiles()) {
					if(file.getName().endsWith(".yml") && file.getName().toLowerCase().startsWith(args[1]))
						list.add(file.getName().substring(0, file.getName().length() - 4));
				}
			}
			
			if(args[0].equals("create") && sender.hasPermission("customhealth.commands.create")) {
				for(int x = 1; x <= 25; x++)
					list.add(String.valueOf(x));
			}
		}
		
		return list;
	}
}
