package at.felixfritz.customhealth.util;

import java.util.Random;


public class FloatValue {
	
	public float min = 0;
	public float max = 0;
	
	public FloatValue() {
		this.min = 0;
		this.max = 0;
	}
	
	public FloatValue(float value) {
		this.min = value;
		this.max = value;
	}
	
	public FloatValue(float min, float max) {
		this.min = Math.min(min, max);
		this.max = Math.max(min, max);
	}
	
	public FloatValue(float[] array) {
		if(array.length > 0) {
			this.min = UselessMath.getMin(array);
			this.max = UselessMath.getMax(array);
		}
	}
	
	public float getMin() {
		return min;
	}
	
	public void setMin(float x) {
		if(x <= max)
			min = x;
		else {
			min = max;
			max = x;
		}
	}
	
	public float getMax() {
		return max;
	}
	
	public void setMax(float x) {
		if(x >= min)
			max = x;
		else {
			max = min;
			min = x;
		}
	}
	
	public FloatValue decrementAll() {
		max--;
		min--;
		return this;
	}
	
	public FloatValue incrementAll() {
		max++;
		min++;
		return this;
	}
	
	public FloatValue divideBy(float that) {
		max /= that;
		min /= that;
		return this;
	}
	
	public float getNum() {
		return new Random().nextFloat() * (max - min) + min;
	}
	
	public String toString() {
		if(min != max)
			return min + "/" + max;
		return String.valueOf(min);
	}
	
}
