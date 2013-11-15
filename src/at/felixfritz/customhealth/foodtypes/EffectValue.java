package at.felixfritz.customhealth.foodtypes;

import org.bukkit.entity.Player;

public abstract class EffectValue {
	
	private final String name;
	private float probability;
	private final String[] parameters;
	
	public EffectValue(String name, String[] parameters) {
		if(name == null)
			throw new NullPointerException("Name can not be null!");
		
		this.name = name;
		this.parameters = parameters;
		this.probability = 1f;
	}
	
	public final String getName() {
		return name;
	}
	
	public final String[] getParameters() {
		return parameters;
	}
	
	public final void setProbability(float probability) {
		if(probability >= 0 && probability <= 1)
			this.probability = probability;
	}
	
	public final void setProbability(int percentage) {
		setProbability(((float) percentage) / 100f);
	}
	
	public final float getProbability() {
		return probability;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof String)
			return ((String) o).equalsIgnoreCase(name);
		if(o instanceof EffectValue)
			return ((EffectValue) o).name.equalsIgnoreCase(name);
		return false;
	}
	
	public abstract void applyEffect(Player p);
	
}
