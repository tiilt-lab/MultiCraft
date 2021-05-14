package com.multicraft;

import org.json.simple.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandsQueue {
	Queue<JSONObject> commandsQ = new ConcurrentLinkedQueue<JSONObject>();
	Queue<String> commands = new ConcurrentLinkedQueue<String>();
	private static CommandsQueue instance = null;
	
	public static CommandsQueue getInstance() {
		if(instance == null) {
			instance = new CommandsQueue();
		}
		return instance;
	}
}
