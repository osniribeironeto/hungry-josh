package com.osni.world;

public class Camera {
	
	public static int x=0;
	public static int y=0;
	
	public static int clamp(int curr, int min, int max) {
		if (curr < min) {
			curr = min;
		}
		
		if (curr > max) {
			curr = max;
		}
		
		return curr;
	}

}
