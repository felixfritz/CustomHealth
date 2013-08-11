package at.felixfritz.customhealth.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;
import at.felixfritz.customhealth.foodtypes.FoodValue;
import at.felixfritz.customhealth.util.Converter;

/**
 * The class that is being called from CustomHealth for the only command available yet: /chealth
 * @author felixfritz
 */
public class CommandMain implements CommandExecutor, TabCompleter {
	
	private ChatColor descr = ChatColor.GRAY;
	private ChatColor highlight = ChatColor.DARK_GREEN;
	private String suffix = descr + "/ch " + highlight;
	
	//Instance of the main plugin
	private CustomHealth plugin;
	//Instance for the CommandSender
	private CommandSender sender;
	
	/**
	 * Constructor. Initializes the CustomHealth instance,
	 * called with 'new CustomCommand' in the CustomHealth class
	 * @param plugin
	 */
	public CommandMain() {
		this.plugin = CustomHealth.getPlugin();
	}
	
	
	/**
	 * Player typed in a command, do something!
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		this.sender = sender;
		
		
		/*
		 * Check first, if the player has any permission to avoid unnecessary coding
		 */
		if(!hasAnyPermission()) {
			sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
			return true;
		}
		
		if(args.length > 0) {
			
			/*
			 * The '/chealth get'-command is being called, do something
			 */
			if((args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("get")) && sender.hasPermission("customhealth.commands.info") && 
					!(sender instanceof ConsoleCommandSender))
				return getCommand(args);
			
			
			/*
			 * Set command
			 */
			if(args[0].equalsIgnoreCase("set") && sender.hasPermission("customhealth.commands.set") && !(sender instanceof ConsoleCommandSender))
				return setCommand(args);
			
			
			/*
			 * Reset everything to it's original value
			 */
			if(args[0].equalsIgnoreCase("reset") && sender.hasPermission("customhealth.commands.reset"))
				return CommandReset.reset(sender);
			
			
			/*
			 * Get information about this plugin
			 */
			if(args[0].equalsIgnoreCase("plugin") && sender.hasPermission("customhealth.commands.info"))
				return CommandPlugin.sendInfo(sender);
			
			
			/*
			 * Reload the plugin
			 */
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("customhealth.commands.reload")) {
				CustomHealth.reloadPlugin();
				Messenger.sendMessage(ChatColor.GREEN + "CustomHealth reloaded.", sender);
				return true;
			}
			
			
			/*
			 * Create template
			 */
			if(args[0].equalsIgnoreCase("create") && sender.hasPermission("customhealth.commands.create")) {
				int amount = 1;
				if(args.length > 1) {
					try {
						amount = Integer.parseInt(args[1]);
					} catch(Exception e) {}
				}
				
				char letter = 'A';
				for(int x = 0; x < amount; x++) {
					String fileName = CustomHealth.getResourcePath() + "worlds/group" + letter + ".yml";
					
					while(new File(fileName).exists()) {
						letter++;
						if(letter > 'Z') {
							sender.sendMessage(ChatColor.RED + "There are already 26 config files!");
							sender.sendMessage(ChatColor.RED + "Sadly, the alphabet only has 26 letters...");
							sender.sendMessage(ChatColor.RED + "Rename some files manually if you need more.");
							return true;
						}
						fileName = CustomHealth.getResourcePath() + "worlds/group" + letter + ".yml";
					}
					
					CustomHealth.getPlugin().saveResource("template0x0159.yml", true);
					new File(CustomHealth.getResourcePath() + "template0x0159.yml").renameTo(new File(fileName));
					
					sender.sendMessage(ChatColor.GREEN + "Created " + ChatColor.DARK_GREEN + "group" + letter + ".yml " + ChatColor.GREEN + "file.");
				}
				return true;
			}
		}
		
		
		sendHelpMenu();
		
		return true;
	}
	
	
	/**
	 * The generic help menu for /chealth.
	 * The list is going to be extended in the future.
	 */
	private void sendHelpMenu() {
		sender.sendMessage(descr + "-----------[ " + highlight + plugin.getDescription().getName() + descr + " ]-----------");
		if(sender.hasPermission("customhealth.commands.info") && !(sender instanceof ConsoleCommandSender))
			sender.sendMessage(suffix + "info" + descr + ": Get health/food value/effects from item.");
		if(sender.hasPermission("customhealth.commands.set") && !(sender instanceof ConsoleCommandSender))
			sender.sendMessage(suffix + "set" + descr + ": Set value for item in hand.");
		if(sender.hasPermission("customhealth.commands.create"))
			sender.sendMessage(suffix + "create (#)" + descr + ": Create new template file.");
		if(sender.hasPermission("customhealth.commands.reload"))
			sender.sendMessage(suffix + "reload" + descr + ": Reload the config file.");
		if(sender.hasPermission("customhealth.commands.reset"))
			sender.sendMessage(suffix + "reset" + descr + ": Reset all food items to their original value.");
		if(sender.hasPermission("customhealth.commands.plugin"))
			sender.sendMessage(suffix + "plugin" + descr + ": Informations about CustomHealth.");
	}
	
	
	
	/**
	 * The get command
	 * @param args
	 * @return true... always...
	 */
	private boolean getCommand(String[] args) {
		
		Player p = (Player) sender;
		//Check, if the player hasn't added any arguments to '/chealth info'
		if(args.length == 1 && sender instanceof Player) {
			
			//Check first, if the item in hand is edible. If so,
			//call the informPlayer method in the InfoCommand class.
			ItemStack s = p.getItemInHand();
			Material m = s.getType();
			if(m.isEdible() || m.equals(Material.CAKE_BLOCK) || m.equals(Material.MILK_BUCKET))
				CommandInfo.informPlayer(p, s);
			else
				Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible").replaceAll("<food>", m.toString()), p);
			return true;
			
		}
		
		//If the player has added an argument, then he'll get the information about the food in the 2. argument
		if(args.length == 2) {
			try {
				ItemStack stack = Converter.stringToItemStack(args[1]);
				
				Material type = stack.getType();
				if(type.isEdible() || type.equals(Material.CAKE_BLOCK))
					CommandInfo.informPlayer(p, stack);
				else
					Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible").replaceAll("<food>", args[1]), sender);
			} catch(NullPointerException e) {
				Messenger.sendMessage(plugin.getConfig().getString("messages.not-a-food-item").replaceAll("<food>", args[1]), sender);
			}
			return true;
		}
		
		sendGetHelpMenu();
		return true;
	}
	
	
	/**
	 * The help menu for /chealth get
	 */
	private void sendGetHelpMenu() {
		sender.sendMessage(descr + "-----------[ " + highlight + plugin.getDescription().getName() + " /get" + descr + " ]-----------");
		sender.sendMessage(suffix + "info" + descr + ": Get the values of the food item in hand.");
		sender.sendMessage(suffix + "info [food]" + descr + ": Get the values about [food].");
	
	}
	
	
	/**
	 * Set values of the food item
	 * @param args
	 * @return true... always...
	 */
	private boolean setCommand(String[] args) {
		
		if(args.length > 2 && (args.length % 2) == 1) {
			try {
				Player p = (Player) sender;
				
				ItemStack stack = p.getItemInHand();
				FoodValue value = FoodDataBase.getFoodValue(p.getWorld(), Converter.itemStackToString(stack));
				
				String hearts = value.getRegenHearts().toString();
				String hunger = value.getRegenHunger().toString();
				
				if(stack.getItemMeta().hasLore()) {
					List<String> list = stack.getItemMeta().getLore();
					for(String s : list) {
						if(s.endsWith(" hearts"))
							hearts = s.substring(2, s.length() - 7);
						else if(s.endsWith(" food bars"))
							hunger = s.substring(2, s.length() - 10);
					}
				}
				
				for(int x = 1; x < args.length; x += 2) {
					if("hearts".contains(args[x].toLowerCase()) || "health".contains(args[x].toLowerCase()))
						hearts = args[x + 1];
					else if("hunger".contains(args[x].toLowerCase()) || "food".contains(args[x].toLowerCase()))
						hunger = args[x + 1];
					else
						sender.sendMessage(ChatColor.RED + "Invalid modifier " + args[x] + ". Can only be \"hearts\" or \"hunger\".");
				}
				CommandSet.setCommandExecuted(sender, hearts, hunger);
				return true;
			} catch(Exception e) {
				sender.sendMessage(ChatColor.RED + "Error: " + e.getLocalizedMessage());
			}
		}
		
		sendSetHelpMenu();
		return true;
	}
	
	
	private void sendSetHelpMenu() {
		sender.sendMessage(descr + "-----------[ " + highlight + plugin.getDescription().getName() + " /set" + descr + " ]-----------");
		sender.sendMessage(suffix + "set hearts <amount>" + descr + ": Hearts regenerated.");
		sender.sendMessage(suffix + "set food <amount>" + descr + ": Food bars regenerated.");
		sender.sendMessage(suffix + "set hearts <amount> food <amount>" + descr + ": Combination of both.");
	}
	
	
	
	/**
	 * Quick check, if the player has any permissions
	 * @return true, if the player has at least one permission
	 */
	private boolean hasAnyPermission() {
		for(Permission permission : plugin.getDescription().getPermissions()) {
			if(sender.hasPermission(permission))
				return true;
		}
		
		return false;
	}
	
	
	
	/**
	 * Tab auto-complete
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		
		if(args.length <= 1) {
			if(("get".startsWith(args[0].toLowerCase()) || "info".startsWith(args[0].toLowerCase())) && sender.hasPermission("customhealth.commands.info"))
				list.add("info");
			if(("set".startsWith(args[0].toUpperCase())) && sender.hasPermission("customhealth.commands.set"))
				list.add("set");
			if("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.reload"))
				list.add("reload");
			if("reset".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.reset"))
				list.add("reset");
			if("plugin".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.plugin"))
				list.add("plugin");
			if("create".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.create"))
				list.add("create");
			
		} else if(args.length == 2 && ("get".startsWith(args[0].toLowerCase()) || "info".startsWith(args[0].toLowerCase())) && sender.hasPermission("customhealth.commands.info")) {
			for(String food : FoodDataBase.getFoodNames()) {
				if(food.toUpperCase().startsWith(args[1].toUpperCase()))
					list.add(food);
			}
		} else if("set".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.set")) {
			int index = args.length - 1;
			if("hearts".startsWith(args[index].toLowerCase()) || "health".startsWith(args[index].toLowerCase()))
				list.add("hearts");
			if("food".startsWith(args[index].toLowerCase()) || "hunger".startsWith(args[index].toLowerCase()))
				list.add("food");
		}
		
		return list;
	}
}
