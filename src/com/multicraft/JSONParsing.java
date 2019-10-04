package com.multicraft;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONParsing {
	public static JSONObject JSONFromString(String str){
		Object obj = JSONValue.parse(str);
		JSONArray array = (JSONArray) obj;
		return (JSONObject) array.get(0);
	}
}
