package com.timkanake.multicraft;

import java.util.HashMap;

public class Materials {
	private static Materials instance = null;
	HashMap<String, Integer> materials = new HashMap<String, Integer>();
	private Materials() {
		String[] mat = {"wood" , "grass", "stone", "water", "lava", "gold"};
		for(String s: mat) {
			materials.put(s, 1);
		}		
	}
	
	public static Materials getInstance() {
		if(instance == null) {
			instance = new Materials();
		}
		return instance;
	}
	
	public int getId(String s) {
		return materials.get(s);
	}

}
