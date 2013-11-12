package at.felixfritz.customhealth.util;

import java.util.Random;

public class RandomValue implements Cloneable {
	
	private double minValue;
	private double maxValue;
	
	public RandomValue(double minValue, double maxValue) {
		if(minValue < maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		} else {
			this.minValue = maxValue;
			this.maxValue = minValue;
		}
	}
	
	public double getMinValue() {
		return minValue;
	}
	
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	public double getRandomValue() {
		return new Random().nextDouble() * getRange() + minValue;
	}
	
	public int getRandomIntValue() {
		return new Random().nextInt((int) getRange() + 1) + (int) minValue - 1;
	}
	
	public double getRange() {
		return Math.abs(maxValue - minValue);
	}
	
	@Override
	public String toString() {
		if(minValue == maxValue)
			return String.valueOf(minValue);
		return minValue + "," + maxValue;
	}
	
	public static RandomValue parseRandomValue(String param) {
		if(param == null) return null;
		
		String[] args = param.replaceAll(" ", "").split(",");
		
		try {
			if(args.length == 1) {
				return new RandomValue(Double.parseDouble(args[0]), Double.parseDouble(args[0]));
			} else {
				return new RandomValue(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
			}
		} catch(NumberFormatException e) {
			return null;
		}
	}
}
