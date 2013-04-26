package at.felixfritz.customhealth.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.CustomHealth;

/**
 * The class that is being called from CustomHealth for the only command available yet: /chealth
 * @author felixfritz
 */
public class CustomCommand implements CommandExecutor {
	
	//Instance of the main plugin
	private CustomHealth plugin;
	//Instance for the CommandSender
	private CommandSender sender;
	
	/**
	 * Constructor. Initializes the CustomHealth instance,
	 * called with 'new CustomCommand' in the CustomHealth class
	 * @param plugin
	 */
	public CustomCommand(CustomHealth plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * Player typed in a command, do something!
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		this.sender = sender;
		
		/*
		 * Check, if the player has the permission to use that /chealth command.
		 */
		if(!sender.hasPermission("customhealth.commands")) {
			Messenger.sendMessage(plugin.getConfig().getString("messages.no-permission").replaceAll("<player>", 
					sender.getName()), sender);
			return true;
		}
		
		
		try {
			
			/*
			 * The '/chealth info'-command is being called, do something
			 */
			if(args[0].equalsIgnoreCase("info")) {
				
				//Check, if the player hasn't added any arguments to '/chealth info'
				if(args.length == 1) {
					
					Player p = (Player) sender;
					//Check first, if the item in hand is edible. If so,
					//call the informPlayer method in the InfoCommand class.
					if(p.getItemInHand().getType().isEdible())
						InfoCommand.informPlayer(p, p.getItemInHand().getType());
					else
						Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible"), p);
					return true;
					
				}
				
				//If the player has added an argument, then he'll get the information about the food in the 2. argument
				if(args.length == 2) {
					
					if(new ItemStack(Material.getMaterial(args[1].toUpperCase())).getType().isEdible())
						InfoCommand.informPlayer(sender, Material.getMaterial(args[1].toUpperCase()));
					else
						Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible"), sender);
				}
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
		ChatColor descr = ChatColor.GRAY;
		ChatColor highlight = ChatColor.RED;
		
		sender.sendMessage(descr + "/chealth " + highlight + "info" + descr + ": Get information about item in hand.");
	}
	
}
