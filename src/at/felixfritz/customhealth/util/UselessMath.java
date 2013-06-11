package at.felixfritz.customhealth.util;

public class UselessMath {
	
	public static int[] stringToIntArray(String word) {
		
		String[] array = word.replaceAll(" ", "").split("/");
		
		if(array.length == 1) {
			try {
				return new int[]{Integer.valueOf(array[0])};
			} catch(NumberFormatException e) {
				return new int[0];
			}
		}
		
		try {
			return new int[]{Integer.valueOf(array[0]), Integer.valueOf(array[1])};
		} catch(Exception e) {
			return new int[0];
		}
		
	}
	
	public static float[] stringToFloatArray(String word) {
		
		String[] array = word.replaceAll(" ", "").split("-");
		
		if(array.length == 1) {
			try {
				return new float[]{Float.valueOf(array[0])};
			} catch(NumberFormatException e) {
				return new float[0];
			}
		}
		
		try {
			return new float[]{Float.valueOf(array[0]), Float.valueOf(array[1])};
		} catch(Exception e) {
			return new float[0];
		}
		
	}
	
	
	public static boolean isNumber(String param) {
		try {
			 Integer.valueOf(param);
			 return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static int getMin(int[] array) {
		int min = Integer.MAX_VALUE;
		for(int x : array) {
			if(x < min)
				min = x;
		}
		return min;
	}
	
	public static int getMax(int[] array) {
		int max = Integer.MIN_VALUE;
		for(int x : array) {
			if(x > max)
				max = x;
		}
		return max;
	}
	
	public static float getMin(float[] array) {
		float min = Float.MAX_VALUE;
		for(float x : array) {
			if(x < min)
				min = x;
		}
		return min;
	}
	
	public static float getMax(float[] array) {
		float max = Float.MIN_VALUE;
		for(float x : array) {
			if(x > max)
				max = x;
		}
		return max;
	}
	
	public static boolean isValidModifier(String modifier, boolean isFloat) {
		for(char c : modifier.toCharArray()) {
			if(!Character.isDigit(c) && c != '-' && c != '/' && (!isFloat || c != '.'))
				return false;
		}
		return true;
	}
}
