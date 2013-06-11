package at.felixfritz.customhealth.util;

import java.util.Random;


public class IntValue {
	
	private int min = 0;
	private int max = 0;
	
	public IntValue() {
		this.min = 0;
		this.max = 0;
	}
	
	public IntValue(int value) {
		this.min = value;
		this.max = value;
	}
	
	public IntValue(int min, int max) {
		this.min = Math.min(min, max);
		this.max = Math.max(min, max);
	}
	
	public IntValue(int[] array) {
		if(array.length > 0) {
			min = UselessMath.getMin(array);
			max = UselessMath.getMax(array);
		}
	}
	
	public int getMin() {
		return min;
	}
	
	public void setMin(int x) {
		if(x <= max)
			min = x;
		else {
			min = max;
			max = x;
		}
	}
	
	public int getMax() {
		return max;
	}
	
	public void setMax(int x) {
		if(x >= min)
			max = x;
		else {
			max = min;
			min = x;
		}
	}
	
	public IntValue decrementAll() {
		max--;
		min--;
		return this;
	}
	
	public IntValue incrementAll() {
		max++;
		min++;
		return this;
	}
	
	public int getNum() {
		return new Random().nextInt(max - min + 1) + min;
	}
	
	public String toString() {
		if(min != max)
			return min + "/" + max;
		return String.valueOf(min);
	}
}
