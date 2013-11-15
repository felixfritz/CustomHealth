package at.felixfritz.customhealth.util;

import java.util.Random;

/**
 * The RandomValue contains 2 doubles, one representing the minimum
 * and the other one the maximum.
 * Random values between those 2 numbers can be determined then.
 * 
 * @author felixfritz
 * @version 0.6
 */
public class RandomValue implements Cloneable {
	
	private double minValue;
	private double maxValue;
	
	/**
	 * Constructor
	 * @param minValue
	 * @param maxValue
	 */
	public RandomValue(double minValue, double maxValue) {
		//Check, if values have to be switched around.
		//Is actually a bit ridiculous here, because setting max and min-values afterwards wouldn't switch it.
		if(minValue < maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		} else {
			this.minValue = maxValue;
			this.maxValue = minValue;
		}
	}
	
	/**
	 * Get the minimum
	 * @return minValue
	 */
	public double getMinValue() {
		return minValue;
	}
	
	/**
	 * Set the minimum
	 * @param minValue
	 */
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	
	/**
	 * Get the maximum
	 * @return maxValue
	 */
	public double getMaxValue() {
		return maxValue;
	}
	
	/**
	 * Set the maximum
	 * @param maxValue
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	/**
	 * Get random value as a double
	 * @return random value between min and max
	 */
	public double getRandomValue() {
		return new Random().nextDouble() * getRange() + minValue;
	}
	
	/**
	 * Get random value as an integer
	 * @return random int value between min and max
	 */
	public int getRandomIntValue() {
		return new Random().nextInt((int) getRange() + 1) + (int) minValue - 1;
	}
	
	/**
	 * Get range
	 * @return max - min
	 */
	public double getRange() {
		return Math.abs(maxValue - minValue);
	}
	
	/*
	 * Might use the toString() method in the future, but not now.
	 */
	/*@Override
	public String toString() {
		if(minValue == maxValue)
			return String.valueOf(minValue);
		return minValue + "," + maxValue;
	}*/
	
	/**
	 * Convert string into a RandomValue.
	 * This is mostly used during load-up in main CustomHealth class.
	 * @param param
	 * @return RandomValue
	 */
	public static RandomValue parseRandomValue(String param) {
		if(param == null) return null;
		
		//Remove all whitespace and split the ','
		String[] args = param.replaceAll(" ", "").split(",");
		
		//If one string inside the args-array can't be converted into a double, it'll throw a NumberFormatException
		try {
			if(args.length == 1)
				return new RandomValue(Double.parseDouble(args[0]), Double.parseDouble(args[0]));
			else
				return new RandomValue(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
		} catch(NumberFormatException e) {
			return null;
		}
	}
}
