package at.felixfritz.customhealth.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.FoodDataBase;
import at.felixfritz.customhealth.foodtypes.FoodValue;

public class CustomCommand implements CommandExecutor {
	
	private CustomHealth plugin;
	private CommandSender sender;
	
	public CustomCommand(CustomHealth plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		this.sender = sender;
		
		if(!sender.hasPermission("customhealth.commands")) {
			Messenger.sendMessage(plugin.getConfig().getString("messages.no-permission").replaceAll("<player>", 
					sender.getName()), sender);
			return true;
		}
		
		
		try {
			
			if(args.length == 0) {
				int x = 0;
				for(FoodValue mat : FoodDataBase.foods) {
					System.out.println(mat.getName() + ": " + mat.getRegenHearts() + " hearts, " + mat.getRegenHunger() + " hunger.");
					x++;
				}
				System.out.println(x + " foods.");
				return true;
			}
			if(args[0].equalsIgnoreCase("info")) {
				if(args.length == 1) {
					Player p = (Player) sender;
					if(p.getItemInHand().getType().isEdible())
						InfoCommand.informPlayer(p, p.getItemInHand().getType());
					else
						Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible"), p);
					return true;
				}
				if(args.length == 2) {
					if(new ItemStack(Material.getMaterial(args[1].toUpperCase())).getType().isEdible())
						InfoCommand.informPlayer(sender, Material.getMaterial(args[1].toUpperCase()));
					else
						Messenger.sendMessage(plugin.getConfig().getString("messages.item-not-edible"), sender);
				}
			}
			
		} catch(ArrayIndexOutOfBoundsException e){
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		sendHelpMenu();
		
		return true;
	}
	
	
	private void sendHelpMenu() {
		ChatColor descr = ChatColor.GRAY;
		ChatColor highlight = ChatColor.RED;
		
		sender.sendMessage(descr + "/chealth " + highlight + "info" + descr + ": Get information about item in hand.");
	}
	
}
