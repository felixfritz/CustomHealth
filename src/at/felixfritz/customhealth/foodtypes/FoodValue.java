package at.felixfritz.customhealth.foodtypes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import at.felixfritz.customhealth.util.RandomValue;

public class FoodValue {
	
	private Material material;
	private byte dataValue;
	private RandomValue regenHearts;
	private RandomValue regenHunger;
	private RandomValue saturation;
	
	private List<EffectValue> effects;
	private List<PotionValue> potions;
	
	private boolean cancel;
	private boolean override;
	
	public FoodValue(Material material, byte dataValue) {
		this(material, dataValue, new RandomValue(0, 0), new RandomValue(0, 0), new RandomValue(0, 0));
	}
	
	public FoodValue(Material material, byte dataValue, RandomValue regenHearts, RandomValue regenHunger) {
		this(material, dataValue, regenHearts, regenHunger, new RandomValue(0, 0));
	}
	
	public FoodValue(Material material, byte dataValue, RandomValue regenHearts, RandomValue regenHunger, RandomValue saturation) {
		
		if(material == null || regenHearts == null || regenHunger == null || saturation == null)
			throw new NullPointerException("Parameters may not be null!");
		
		this.material = material;
		this.dataValue = dataValue;
		this.regenHearts = regenHearts;
		this.regenHunger = regenHunger;
		this.saturation = saturation;
		
		this.effects = new ArrayList<EffectValue>();
		this.potions = new ArrayList<PotionValue>();
		
		this.cancel = false;
		this.override = false;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getDataValue() {
		return dataValue;
	}
	
	public RandomValue getRegenHearts() {
		return regenHearts;
	}
	
	public void setRegenHearts(RandomValue regenHearts) {
		if(regenHearts != null)
			this.regenHearts = regenHearts;
	}
	
	public RandomValue getRegenHunger() {
		return regenHunger;
	}
	
	public void setRegenHunger(RandomValue regenHunger) {
		if(regenHunger != null)
			this.regenHunger = regenHunger;
	}
	
	public RandomValue getSaturation() {
		return saturation;
	}
	
	public void setSaturation(RandomValue saturation) {
		if(saturation != null)
			this.saturation = saturation;
	}
	
	public List<EffectValue> getEffects() {
		return effects;
	}
	
	public void addEffect(EffectValue effect) {
		if(effect != null)
			effects.add(effect);
	}
	
	public EffectValue getEffect(String name) {
		int x;
		if((x = effects.indexOf(name)) >= 0)
			return effects.get(x);
		return null;
	}
	
	public List<PotionValue> getPotionEffects() {
		return potions;
	}
	
	public void addPotionEffect(PotionValue potion) {
		if(potion != null)
			potions.add(potion);
	}
	
	public boolean isCancelled() {
		return this.cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public boolean isOverrideEnabled() {
		return this.override;
	}
	
	public void setOverrideEnabled(boolean override) {
		this.override = override;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		
		if(o instanceof Material)
			return o == material;
		if(o instanceof ItemStack)
			return ((ItemStack) o).getType() == material && ((ItemStack) o).getData().getData() == dataValue;
		if(o instanceof FoodValue)
			return ((FoodValue) o).material == material && ((FoodValue) o).dataValue == dataValue;
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append(material).append('@').append(dataValue).append(", ").append(regenHearts.toString()).append(" hearts, ")
				.append(regenHunger.toString()).append(" food bars, ").append(saturation.toString()).append(" saturation, ")
				.append(effects.size()).append(" effects");
		
		if(effects.size() > 0) {
			sb.append(" (");
			for(EffectValue v : effects) {
				sb.append(v.getName()).append(", ");
			}
			sb.delete(sb.length() - 2, sb.length()).append(')');
		}
		
		sb.append(", ").append(potions.size() + " potion effects");
		
		if(potions.size() > 0) {
			sb.append(" (");
			for(PotionValue v : potions) {
				sb.append(v.getPotion()).append(", ");
			}
			sb.delete(sb.length() - 2, sb.length()).append(')');
		}
		
		return sb.append('.').toString();
	}
	
}
