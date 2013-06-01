package at.felixfritz.customhealth.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;

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
			if(args[0].equalsIgnoreCase("info") && sender.hasPermission("customhealth.commands.info"))
				return getCommand(args);
			
			
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
		if(sender.hasPermission("customhealth.commands.info"))
			sender.sendMessage(suffix + "info" + descr + ": Get health/food value/effects from item.");
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

		//Check, if the player hasn't added any arguments to '/chealth info'
		if(args.length == 1 && sender instanceof Player) {
			
			Player p = (Player) sender;
			//Check first, if the item in hand is edible. If so,
			//call the informPlayer method in the InfoCommand class.
			ItemStack s = p.getItemInHand();
			Material m = s.getType();
			if(m.isEdible() || m.equals(Material.CAKE_BLOCK))
				CommandInfo.informPlayer(p, s);
			else
				Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible").replaceAll("<food>", m.toString()), p);
			return true;
			
		}
		
		//If the player has added an argument, then he'll get the information about the food in the 2. argument
		if(args.length == 2) {
			try {
				ItemStack stack = new ItemStack((args[1].equalsIgnoreCase("enchanted_golden_apple")) ? Material.valueOf("GOLDEN_APPLE") : 
					Material.valueOf(args[1].toUpperCase()));
				
				if(args[1].equalsIgnoreCase("enchanted_golden_apple"))
					stack.getData().setData((byte) 1);
				
				Material type = stack.getType();
				if(type.isEdible() || type.equals(Material.CAKE_BLOCK))
					CommandInfo.informPlayer(sender, stack);
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
			if("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.reload"))
				list.add("reload");
			if("reset".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.reset"))
				list.add("reset");
			if("plugin".startsWith(args[0].toLowerCase()) && sender.hasPermission("customhealth.commands.plugin"))
				list.add("plugin");
			
		} else if(args.length == 2) {
			for(String food : FoodDataBase.getFoodNames()) {
				if(food.toUpperCase().startsWith(args[1].toUpperCase()))
					list.add(food);
			}
		}
		
		return list;
	}
}
