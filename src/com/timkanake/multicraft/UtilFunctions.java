package com.timkanake.multicraft;

import java.util.HashMap;

public class UtilFunctions {
	static HashMap<String, Integer> conversionMap = new HashMap<String, Integer>();
	
	// TODO
	public static int textToInt(String text) throws Exception {
		for(String s: text.split(" ")) {
			if(! conversionMap.containsKey(s)) {
				throw new Exception();
			}
			
			
		}
		return 0;
	}

}
