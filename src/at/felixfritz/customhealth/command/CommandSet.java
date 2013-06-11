package at.felixfritz.customhealth.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import at.felixfritz.customhealth.util.UselessMath;

public class CommandSet {
	
	public static void setCommandExecuted(CommandSender sender, String hearts, String hunger) {
		
		if(sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if(!UselessMath.isValidModifier(hearts, false)) {
				p.sendMessage(ChatColor.RED + "Invalid modifier " + ChatColor.DARK_RED + hearts + ChatColor.DARK_RED + "!");
				p.sendMessage(ChatColor.RED + "Can only be a negative or positive whole number.");
				p.sendMessage(ChatColor.RED + "min/max has to be seperated with '" + ChatColor.DARK_RED + "/" + ChatColor.RED + "'.");
				return;
			}
			if(!UselessMath.isValidModifier(hunger, false)) {
				p.sendMessage(ChatColor.RED + "Invalid modifier " + ChatColor.DARK_RED + hunger + ChatColor.DARK_RED + "!");
				p.sendMessage(ChatColor.RED + "Can only be a negative or positive whole number.");
				p.sendMessage(ChatColor.RED + "min/max has to be seperated with '" + ChatColor.DARK_RED + "/" + ChatColor.RED + "'.");
				return;
			}
			
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.RED + hearts + " hearts");
			lore.add(ChatColor.RED + hunger + " food bars");
			
			ItemMeta meta = p.getItemInHand().getItemMeta();
			meta.setLore(lore);
			p.getItemInHand().setItemMeta(meta);
			p.sendMessage(ChatColor.GREEN + "Set to " + ChatColor.DARK_GREEN + hearts + " hearts" + ChatColor.GREEN + " and " + ChatColor.DARK_GREEN + hunger + " food bars" + ChatColor.GREEN + "!");
			
		}
	}
	
}
