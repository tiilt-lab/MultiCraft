package com.multicraft.util;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONParser {

	public static JSONObject JSONFromString(String str) {
		return (JSONObject) JSONValue.parse(str);
	}

}
