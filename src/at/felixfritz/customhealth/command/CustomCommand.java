package at.felixfritz.customhealth.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;

/**
 * The class that is being called from CustomHealth for the only command available yet: /chealth
 * @author felixfritz
 */
public class CustomCommand implements CommandExecutor, TabCompleter {
	
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
	public CustomCommand() {
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
		
		try {
			
			/*
			 * The '/chealth get'-command is being called, do something
			 */
			if(args[0].equalsIgnoreCase("get") && sender.hasPermission("customhealth.commands.get")) {
				
				//Check, if the player hasn't added any arguments to '/chealth info'
				if(args.length == 1) {
					
					Player p = (Player) sender;
					//Check first, if the item in hand is edible. If so,
					//call the informPlayer method in the InfoCommand class.
					Material m = p.getItemInHand().getType();
					if(m.isEdible() || m.equals(Material.CAKE_BLOCK))
						GetCommand.informPlayer(p, m);
					else
						Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible").replaceAll("<food>", m.toString()), p);
					return true;
					
				}
				
				//If the player has added an argument, then he'll get the information about the food in the 2. argument
				if(args.length == 2) {
					try {
						String item = (args[1].equalsIgnoreCase("cake_slice")) ? "CAKE_BLOCK" : args[1].toUpperCase();
						Material type = Material.getMaterial(item.toUpperCase());
						if(type.isEdible() || type.equals(Material.CAKE_BLOCK))
							GetCommand.informPlayer(sender, Material.getMaterial(args[1].toUpperCase()));
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
			
			
			/*
			 * A not very smart execution of the set command...
			 */
			if(args[0].equalsIgnoreCase("set") && sender.hasPermission("customhealth.commands.set")) {
				String item = null;
				Map<String, Integer> values = new HashMap<String, Integer>();
				
				/*
				 * An odd number of arguments: /ch set food 2 (set = args[0], food = args[1], 2 = args[2])
				 * The sender has to be a player, we will be looking at the item he has in his hands, since he didn't specify it in the command
				 */
				if(((args.length % 2) == 1 && args.length > 2) && sender instanceof Player) {
					Player p = (Player) sender;
					Material type = p.getItemInHand().getType();
					
					//Check if the item in his hand is edible, if not, 
					if(type.isEdible() || type.equals(Material.CAKE_BLOCK)) {
						
						//Yes, the item is edible, set the item string to the itemname and look for the hearts and food values
						item = type.name();
						
						for(int x = 1; x < args.length; x += 2) {
							try {
								if("hearts".contains(args[x]))
									values.put("hearts", Integer.valueOf(args[x + 1]));
								else if("food".contains(args[x]) || "hunger".contains(args[x]))
									values.put("food", Integer.valueOf(args[x + 1]));
								else
									SetCommand.sendMessage(sender, false, item, args[x], Integer.valueOf(args[x + 1]));
							} catch(Exception e) {
								SetCommand.sendMessage(sender, false, item, args[x], Integer.valueOf(args[x + 1]));
							}
						}
					} else {
						Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible").replaceAll("<food>", p.getItemInHand().getType().name()), sender);
					}
				} else if((args.length % 2) == 0 && args.length > 3) {
					
					//Almost the same as above, except that now the user is specifying the item name
					Material mat = Material.valueOf(args[1].toUpperCase());
					if(mat.isEdible() || mat.equals(Material.CAKE_BLOCK)) {
						item = args[1];
						for(int x = 2; x < args.length; x += 2) {
							
							try {
								if("hearts".contains(args[x]) || "health".contains(args[x]))
									values.put("hearts", Integer.valueOf(args[x + 1]));
								else if("food".contains(args[x]) || "hunger".contains(args[x]))
									values.put("food", Integer.valueOf(args[x + 1]));
								else
									SetCommand.sendMessage(sender, false, item, args[x], Integer.valueOf(args[x + 1]));
							} catch(Exception e) {
								e.printStackTrace();
								SetCommand.sendMessage(sender, false, item, args[x], Integer.valueOf(args[x + 1]));
							}
						}
					} else {
						Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible").replaceAll("<food>", args[1]), sender);
					}
				}
				
				if(item == null || values.size() == 0)
					sendSetHelpMenu();
				else
					SetCommand.executeSetCommand(sender, item, values);
				
				return true;
			} //End of the set command
			
			
			if(args[0].equalsIgnoreCase("reset") && sender.hasPermission("customhealth.commands.reset")) {
				ResetCommand.reset(sender);
				return true;
			}
			
			
			if(args[0].equalsIgnoreCase("info") && sender.hasPermission("customhealth.commands.info")) {
				InfoCommand.sendInfo(sender);
				return true;
			}
			
			
			if(args[0].equalsIgnoreCase("reload")) {
				CustomHealth.reloadPlugin();
				Messenger.sendMessage(ChatColor.GREEN + "CustomHealth reloaded.", sender);
				return true;
			}
			
		} catch(ArrayIndexOutOfBoundsException e){
			//I know that it was an OutOfBoundsException, so I prepared this. Nothing will happen,
			//when an index outside of the arraylength is being called
		} catch(Exception e) {
			//Okay, now we have a problem. What just happened?
			e.printStackTrace();
		}
		
		
		sendHelpMenu();
		
		return true;
	}
	
	
	/**
	 * The generic help menu for /chealth.
	 * The list is going to be extended in the future.
	 */
	private void sendHelpMenu() {
		if(sender.hasPermission("customhealth.commands.help")) {
			sender.sendMessage(descr + "-----------[ " + highlight + plugin.getDescription().getName() + descr + " ]-----------");
			if(sender.hasPermission("customhealth.commands.set"))
				sender.sendMessage(suffix + "set" + descr + ": Set health/food value for food item.");
			if(sender.hasPermission("customhealth.commands.get"))
				sender.sendMessage(suffix + "get" + descr + ": Get health/food value/effects from item.");
			if(sender.hasPermission("customhealth.commands.reload"))
				sender.sendMessage(suffix + "reload" + descr + ": Reload the config file.");
			if(sender.hasPermission("customhealth.commands.reset"))
				sender.sendMessage(suffix + "reset" + descr + ": Reset all food items to their original value.");
			if(sender.hasPermission("customhealth.commands.info"))
				sender.sendMessage(suffix + "info" + descr + ": More informations about CustomHealth.");
		} else {
			sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
		}
	}
	
	
	/**
	 * The help menu for /chealth get
	 */
	private void sendGetHelpMenu() {
		if(sender.hasPermission("customhealth.commands.get")) {
			sender.sendMessage(descr + "-----------[ " + highlight + plugin.getDescription().getName() + " /get" + descr + " ]-----------");
			sender.sendMessage(suffix + "get" + descr + ": Get the values of the food item in hand.");
			sender.sendMessage(suffix + "get [food]" + descr + ": Get the values about [food].");
		} else {
			sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
		}
		
	}
	
	
	
	private void sendSetHelpMenu() {
		if(sender.hasPermission("customhealth.commands.set")) {
			sender.sendMessage(descr + "-----------[ " + highlight + plugin.getDescription().getName() + " /get" + descr + " ]-----------");
			sender.sendMessage(suffix + "set [food] hearts [value]" + descr + ": Set the hearts value for the food item.");
			sender.sendMessage(suffix + "set [food] food [value]" + descr + ": Set the hunger value for the food item.");
			sender.sendMessage(suffix + "set hearts [value]" + descr + ": Set regen value for the item in your hand.");
			sender.sendMessage(suffix + "set hearts [value] food [value]" + descr + ": Combined arguments.");
		} else {
			sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
		}
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


	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		
		if(args.length <= 1) {
			try {
				if("get".startsWith(args[0]))
					list.add("get");
				if("set".startsWith(args[0]))
					list.add("set");
			} catch(Exception e) {
				list.add("get");
				list.add("set");
			}
		} else if(args.length == 2) {
			for(String food : FoodDataBase.getFoodNames()) {
				if(food.toUpperCase().startsWith(args[1].toUpperCase()))
					list.add(food);
			}
		} else if(args.length > 2) {
			try {
				if("hearts".startsWith(args[args.length - 1].toLowerCase()))
					list.add("hearts");
				if("food".startsWith(args[args.length - 1].toLowerCase()) || "hunger".startsWith(args[args.length - 1].toLowerCase()))
					list.add("food");
			} catch(NullPointerException e) {
				list.add("hearts");
				list.add("food");
			}
		}
		
		return list;
	}
}
