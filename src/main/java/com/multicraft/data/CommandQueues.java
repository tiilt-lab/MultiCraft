package com.multicraft.data;

import org.json.simple.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandQueues {

	private final Queue<JSONObject> jsonQueue = new ConcurrentLinkedQueue<>();
	private final Queue<String> strQueue = new ConcurrentLinkedQueue<>();

	private static CommandQueues instance = null;
	
	public static CommandQueues getInstance() {
		if (instance == null) {
			instance = new CommandQueues();
		}
		return instance;
	}

	public void addString(String command) {
		strQueue.add(command);
	}

	public String consumeString() {
		return strQueue.remove();
	}

	public boolean containsStrings() {
		return !strQueue.isEmpty();
	}

	public void addObject(JSONObject command) {
		jsonQueue.add(command);
	}

	public JSONObject consumeObject() {
		return jsonQueue.remove();
	}

	public boolean containsObjects() {
		return !jsonQueue.isEmpty();
	}

}
