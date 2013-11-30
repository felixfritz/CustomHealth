package at.felixfritz.customhealth.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import at.felixfritz.customhealth.CustomHealth;
import at.felixfritz.customhealth.foodtypes.EffectValue;
import at.felixfritz.customhealth.foodtypes.FoodValue;
import at.felixfritz.customhealth.foodtypes.PotionValue;
import at.felixfritz.customhealth.foodtypes.effects.EffectBurn;
import at.felixfritz.customhealth.foodtypes.effects.EffectClear;
import at.felixfritz.customhealth.foodtypes.effects.EffectCmd;
import at.felixfritz.customhealth.foodtypes.effects.EffectExplosion;
import at.felixfritz.customhealth.foodtypes.effects.EffectXP;
import at.felixfritz.customhealth.util.RandomValue;

public class FoodCreator {
	
	protected static boolean answerMade(Player p, CommandMain.FoodCreators creator, String argument) {
		if(argument.equalsIgnoreCase("quit")) {
			p.sendMessage(ChatColor.RED + "Quit without saving.");
			return false;
		}
		
		ChatColor highlight = ChatColor.GOLD;
		ChatColor lowlight = ChatColor.YELLOW;
		
		for(int x = 0; x < 10; x++) p.sendMessage("");
		
		switch(creator.step) {
		
		//verification question, do you want to start with  the questions?
		case 0: return step0(p, creator, argument, highlight, lowlight);
		
		//set amount of hearts
		case 1: return step1(p, creator, argument, highlight, lowlight);
		
		//set amount of hunger regen
		case 2: return step2(p, creator, argument, highlight, lowlight);
		
		//set amount of saturation
		case 3: return step3(p, creator, argument, highlight, lowlight);
		
		//add potion effects
		case 4: return step4(p, creator, argument, highlight, lowlight);
		
		//add additional effects
		case 5: return step5(p, creator, argument, highlight, lowlight);
		
		}
		
		return false;
	}
	
	/**
	 * Check, if player really wants to enter the editor
	 * @return false, if he typed in "/ch no"
	 */
	private static boolean step0(Player p, CommandMain.FoodCreators creator, String argument, ChatColor highlight, ChatColor lowlight) {
		if(argument.equalsIgnoreCase("yes")) {
			
			p.sendMessage(highlight + "/ch #" + lowlight + ": Skip one step");
			p.sendMessage(highlight + "/ch quit" + lowlight + ": Quit editor (without saving)");
			p.sendMessage(lowlight + "Arguments in brackets (" + highlight + "[ ]" + lowlight + ") are optional.");
			creator.step++;
			step1(p, creator, "", highlight, lowlight);
			return true;
			
		} else if(argument.equalsIgnoreCase("no")) {
			
			p.sendMessage(ChatColor.RED + "Process cancelled.");
			return false;
			
		} else {
			
			p.sendMessage(lowlight + "There doesn't seem to be a food item of the type " + creator.value.getMaterial() + " with the value " 
					+ creator.value.getDataValue() + " in the configuration file. Do you want to create one in game?");
			p.sendMessage(ChatColor.GREEN + "/ch yes");
			p.sendMessage(ChatColor.RED + "/ch no");
			return true;
			
		}
	}
	
	/**
	 * Set the amount of hearts that the item should regenerate
	 */
	private static boolean step1(Player p, CommandMain.FoodCreators creator, String argument, ChatColor highlight, ChatColor lowlight) {
		RandomValue value;
		if((value = RandomValue.parseRandomValue(argument)) != null) {
			
			creator.value.setRegenHearts(value);
			p.sendMessage(lowlight + "Set heart regen to " + highlight + argument + lowlight + ".");
			
			creator.step++;
			step2(p, creator, "", highlight, lowlight);
			
		} else if(argument.equals("#")) {
			
			p.sendMessage(lowlight + "Set heart regen to 0.");
			creator.step++;
			step2(p, creator, "", highlight, lowlight);
			
		} else {
			
			p.sendMessage(ChatColor.GRAY + "----- step 1/5 -----");
			p.sendMessage(lowlight + "How much " + highlight + "health" + lowlight + " should be regenerated? ('" + highlight + "/ch min[,max]" + lowlight +"')");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch 2" + lowlight + " - regenerate 1 heart");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch 0,10" + lowlight + " - regenerate up to 5 hearts (can be 0)");
			
		}
		
		return true;
	}
	
	/**
	 * How many foodbar units should be refilled?
	 */
	private static boolean step2(Player p, CommandMain.FoodCreators creator, String argument, ChatColor highlight, ChatColor lowlight) {
		RandomValue value;
		if((value = RandomValue.parseRandomValue(argument)) != null) {
			
			creator.value.setRegenHunger(value);
			p.sendMessage(lowlight + "Set food regen to " + highlight + argument + lowlight + ".");
			creator.step++;
			step3(p, creator, "", highlight, lowlight);
			
		} else if(argument.equals("#")) {
			
			p.sendMessage(lowlight + "Set food regen to 0.");
			creator.step++;
			step3(p, creator, "", highlight, lowlight);
			
		} else {
			
			p.sendMessage(ChatColor.GRAY + "----- step 2/5 -----");
			p.sendMessage(lowlight + "How many " + highlight + "hunger units" + lowlight + " should be refilled?");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch 2" + lowlight + " - refill 1 hunger unit");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch 2,5" + lowlight + " - refill 1, 1.5, 2 or 2.5 hunger units");
			
		}
		
		return true;
	}
	
	/**
	 * How much saturation shauld saturate is asked here
	 */
	private static boolean step3(Player p, CommandMain.FoodCreators creator, String argument, ChatColor highlight, ChatColor lowlight) {
		RandomValue value;
		if((value = RandomValue.parseRandomValue(argument)) != null) {
			
			creator.value.setRegenHunger(value);
			p.sendMessage(lowlight + "Set saturation to " + highlight + argument + lowlight + ".");
			creator.step++;
			step4(p, creator, "", highlight, lowlight);
			
		} else if(argument.equals("#")) {
			
			p.sendMessage(lowlight + "Set saturation to 0.");
			creator.step++;
			step4(p, creator, "", highlight, lowlight);
			
		} else {
			
			p.sendMessage(ChatColor.GRAY + "----- step 3/5 -----");
			p.sendMessage(lowlight + "How much " + highlight + "saturation" + lowlight + " should be refilled?");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch 2" + lowlight + " - give 2 saturation");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch 1,6" + lowlight + " - give between 1 and 6 saturation");
			
		}
		
		return true;
	}
	
	/**
	 * Add potion effects
	 */
	private static boolean step4(Player p, CommandMain.FoodCreators creator, String argument, ChatColor highlight, ChatColor lowlight) {
		PotionValue value;
		if((value = PotionValue.parsePotionValue(argument.split(" "))) != null) {
			
			if(creator.value.getPotionEffects().contains(value)) {
				p.sendMessage(ChatColor.YELLOW + value.getPotion().getName() + " already exists. Replacing it.");
				creator.value.getPotionEffects().remove(value);
			}
			creator.value.addPotionEffect(value);
			p.sendMessage(lowlight + "Added potion effect " + highlight + value.getPotion() + lowlight + ".");
			step4(p, creator, "", highlight, lowlight);
			
		} else if(argument.equals("#")) {
			
			p.sendMessage(lowlight + "Added " + creator.value.getPotionEffects().size() + " potion effect" + ((creator.value.getPotionEffects().size() == 1) ? 
					"." : "s.") + " Moving on.");
			creator.step++;
			step5(p, creator, "", highlight, lowlight);
			
		} else {
			
			p.sendMessage(ChatColor.GRAY + "----- step 4/5 -----");
			p.sendMessage(lowlight + "Add a " + highlight + "potion effect" + lowlight + "!");
			p.sendMessage(highlight + "/ch <type> [strength] [seconds] [chance]");
			p.sendMessage(lowlight + "Go to next step: '/ch #'. Tab-autocomplete feature included.");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch regeneration 2,3 20,50 50%" + lowlight
					+ " - 50% chance to get between 20 and 50 seconds of regeneration 2 or 3");
			
		}
		
		return true;
	}
	
	/**
	 * Add additional effects that don't qualify as potion effects
	 */
	private static boolean step5(final Player p, final CommandMain.FoodCreators creator, String argument, ChatColor highlight, ChatColor lowlight) {
		
		if(argument.equalsIgnoreCase("cancel")) {
			if(creator.value.isCancelled()) {
				p.sendMessage(lowlight + "Was already cancelled. Removing effect again.");
				creator.value.setCancelled(false);
			} else {
				p.sendMessage(lowlight + "Added " + highlight + "cancel" + lowlight + " as an effect.");
				creator.value.setCancelled(true);
			}
			argument = "";
			
		} else if(argument.equalsIgnoreCase("override")) {
			if(creator.value.isOverrideEnabled()) {
				p.sendMessage(lowlight + "Override was already enabled. Removing effect again.");
				creator.value.setOverrideEnabled(false);
			} else {
				p.sendMessage(lowlight + "Added " + highlight + "override" + lowlight + " as an effect.");
				creator.value.setOverrideEnabled(true);
			}
			argument = "";
		}
		
		EffectValue value;
		if((value = parseEffectValue(p, argument)) != null) {
			
			if(creator.value.getEffects().contains(value)) {
				p.sendMessage(ChatColor.YELLOW + value.getName() + " already existed. Replacing it.");
				creator.value.getEffects().remove(value);
			}
			creator.value.addEffect(value);
			p.sendMessage(lowlight + "Added the effect " + highlight + value.getName() + lowlight + ".");
			step5(p, creator, "", highlight, lowlight);
			
		} else if(argument.equals("#")) {
			
			p.sendMessage(lowlight + "Added " + creator.value.getEffects().size() + " effect" + ((creator.value.getEffects().size() == 1) ? 
					"." : "s.") + " Finishing off.");
			Bukkit.getScheduler().runTaskAsynchronously(CustomHealth.getPlugin(), new Runnable() {public void run() {saveFile(p, creator);}});
			return false;
			
		} else if(argument.equalsIgnoreCase("effects")) {
			
			p.sendMessage(highlight + "/ch xp <amount> [chance]" + lowlight + ": Give <amount> of xp to player");
			p.sendMessage(highlight + "/ch burn [seconds] [chance]" + lowlight + ": Set player on fire for an amount of time");
			p.sendMessage(highlight + "/ch explosion [size] [chance]" + lowlight + ": Create an explosion of a certain <size>");
			p.sendMessage(highlight + "/ch clear [chance]" + lowlight + ": Clear effects (does not include effects applied afterwards)");
			p.sendMessage(highlight + "/ch cmd <command>" + lowlight + ": Let player execute the <command>");
			p.sendMessage(highlight + "/ch override" + lowlight + ": Don't apply effects that the player gets afterwards");
			p.sendMessage(highlight + "/ch cancel" + lowlight + ": Don't let the player eat that item. Nothing will happen.");
			
		} else {
			
			p.sendMessage(ChatColor.GRAY + "----- step 5/5 -----");
			p.sendMessage(lowlight + "Add other " + highlight + "effects" + lowlight + "!");
			p.sendMessage(highlight + "/ch <type> [argument1] [argument2] [etc.]");
			p.sendMessage(lowlight + "Finish set-up: '/ch #'. Tab-autocomplete feature included. List effects: /ch effects");
			p.sendMessage(ChatColor.GRAY + "Example: " + highlight + "/ch burn 10,25 80%" + lowlight
					+ " - 80% chance to burn between 10 and 25 seconds.");
			
		}
		
		return true;
	}
	
	private static EffectValue parseEffectValue(Player p, String effect) {
		
		EffectValue ev = null;
		String arg = effect.split("/")[0].toLowerCase();
		if(arg.startsWith("xp")) {
			try {
				ev = new EffectXP(effect.replaceAll(" ", "").substring(3).split(" "));
			} catch(IllegalArgumentException e) {
				p.sendMessage(ChatColor.RED + effect + " is an illegal argument.");
				p.sendMessage(ChatColor.GRAY + "Example: /ch xp 10,100 90% - Give between 10 and 100 xp to player");
			} catch(StringIndexOutOfBoundsException e) {
				p.sendMessage(ChatColor.RED + "xp effects have to have at least 1 argument.");
				p.sendMessage(ChatColor.GRAY + "Example: /ch xp 10,100 90% - Give between 10 and 100 xp to player");
			}
		} else if(arg.startsWith("clear")) {
			if(effect.replaceAll(" ", "").length() > 5)
				ev = new EffectClear(effect.replaceAll(" ", "").substring(6).split(" "));
			else
				ev = new EffectClear(effect.substring(5).split(" "));
		} else if(arg.startsWith("burn")) {
			if(effect.replaceAll(" ", "").length() > 4)
				ev = new EffectBurn(effect.replaceAll(" ", "").substring(5).split(" "));
			else
				ev = new EffectBurn(effect.substring(4).split(" "));
		} else if(arg.startsWith("cmd")) {
			try {
				if(effect.length() > 3)
					ev = new EffectCmd(effect.substring(4).split(" "));
				else
					ev = new EffectCmd(effect.substring(3).split(" "));
			} catch(IllegalArgumentException e) {
				p.sendMessage(ChatColor.RED + "Illegal argument: " + e.getLocalizedMessage() + ". (" + effect + ")");
				p.sendMessage(ChatColor.GRAY + "Example: /ch cmd me is eating an apple");
			}
		} else if(arg.startsWith("explosion")) {
			if(effect.replaceAll(" ", "").length() > 9)
				ev = new EffectExplosion(effect.substring(10).split(" "));
			else
				ev = new EffectExplosion(new String[0]);
		}
		
		return ev;
		
	}
	
	private static void saveFile(Player p, CommandMain.FoodCreators creator) {
		
		File file = new File("plugins/CustomHealth/custom_items.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				p.sendMessage(ChatColor.GOLD + "File \"custom_items.yml\" did not exist yet. Creating it for the first time.");
			} catch (IOException e) {
				p.sendMessage(ChatColor.RED + "File could not be created. I'm really sorry about that. Check the console for more information.");
				e.printStackTrace();
				return;
			}
		}
		
		try {
			
			FoodValue value = creator.value;
			
			Scanner scan = new Scanner(file);
			StringBuilder s = new StringBuilder();
			
			String line;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				if(line.split("@")[0].equalsIgnoreCase(value.getMaterial().name())) {
					byte dataValue = 0;
					if(line.split("@").length == 2) {
						try {
							dataValue = Byte.parseByte(line.split("@")[1]);
						} catch(NumberFormatException e) {}
					}
					
					if(value.getDataValue() == dataValue) {
						s.append("# ").append(line).append('\n');
						
						while(scan.hasNextLine()) {
							line = scan.nextLine();
							if(!line.startsWith("  "))
								break;
							
							s.append("# ").append(line).append('\n');
						}
					}
				}
				s.append(line).append('\n');
			}
			scan.close();
			
			if(s.length() == 0) {
				s.append("# Generated file for custom items.\n# This file is loaded after the plugin goes through the actual world files.\n");
				s.append("# If an identical item with the same datavalue exists in a world configuration file, it will NOT be replaced.\n");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm");
			String date = sdf.format(new Date());
			
			s.append("\n# Custom Item by ").append(p.getName()).append(", creation date: ").append(date).append('\n').append(value.getMaterial());
			if(value.getDataValue() != 0) s.append('@').append(value.getDataValue());
			s.append(':');
			
			if(value.getRegenHearts().getMinValue() != 0 || value.getRegenHearts().getMaxValue() != 0)
				s.append("\n  hearts: ").append(value.getRegenHearts().toString());
			
			if(value.getRegenHunger().getMinValue() != 0 || value.getRegenHunger().getMaxValue() != 0)
				s.append("\n  food: ").append(value.getRegenHunger().toString());
			
			if(value.getSaturation().getMinValue() != 0 || value.getSaturation().getMaxValue() != 0)
				s.append("\n  saturation: ").append(value.getSaturation().toString());
			
			if(!value.getPotionEffects().isEmpty()) {
				s.append("\n  potion: [");
				
				for(PotionValue potion : value.getPotionEffects()) {
					if(s.charAt(s.length() - 1) != '[') s.append(';');
					s.append(potion.getPotion().getName()).append('/').append(potion.getStrength().toString()).append('/').append(potion.getDuration());
					if(potion.getProbability() != 1F)
						s.append('/').append(potion.getProbability());
				}
				
				s.append(']');
			}
			
			if(!value.getEffects().isEmpty()) {
				s.append("\n  effect: [");
				
				for(EffectValue effect : value.getEffects()) {
					if(s.charAt(s.length() - 1) != '[') s.append(';');
					s.append(effect.toString());
				}
				
				s.append(']');
			}
			
			Formatter x = new Formatter(file);
			x.format("%s", s.toString());
			x.flush();
			x.close();
			
			p.sendMessage(ChatColor.GREEN + "Creation successful! Now you only need to reload the plugin! (" 
					+ ChatColor.DARK_GREEN + "/ch reload" + ChatColor.GREEN + ")");
			
		} catch (FileNotFoundException e) {
			p.sendMessage(ChatColor.RED + "File could not be saved! Check console for more information.");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Take some work off of the onTabCompleteMethod in the CommandMain method (since this is a very different field)
	 * @param creator
	 * @param args
	 * @return list of tab-completions
	 */
	protected static List<String> onTabComplete(CommandMain.FoodCreators creator, String[] args) {
		List<String> list = new ArrayList<String>();
		
		switch(creator.step) {
		case 0:
			if("yes".startsWith(args[0])) list.add("yes");
			if("no".startsWith(args[0])) list.add("no");
			return list;
			
		case 1:
		case 2:
			for(int x = -20; x <= 20; x++) {
				if(String.valueOf(x).startsWith(args[0]))
					list.add(String.valueOf(x));
			}
			return list;
			
		case 3:
			Random rand = new Random();
			for(int x = 0; x < 20; x++)
				list.add(String.valueOf(rand.nextFloat()));
			return list;
			
		case 4:
			if(args.length == 1) {
				for(PotionEffectType type : PotionEffectType.values()) {
					if(type != null)
						if(type.getName().toLowerCase().startsWith(args[0]))
							list.add(type.getName());
				}
			} else if(args.length == 2)
				list.add("<strength>");
			else if(args.length == 3)
				list.add("<duration>");
			else if(args.length == 4)
				list.add("<chance>");
			return list;
			
		case 5:
			if(args.length == 1) {
				if("burn".startsWith(args[0])) list.add("burn");
				if("cancel".startsWith(args[0])) list.add("cancel");
				if("clear".startsWith(args[0])) list.add("clear");
				if("cmd".startsWith(args[0])) list.add("cmd");
				if("explosion".startsWith(args[0])) list.add("explosion");
				if("override".startsWith(args[0])) list.add("override");
				if("xp".startsWith(args[0])) list.add("xp");
			}
		}
		
		return list;
	}
}
