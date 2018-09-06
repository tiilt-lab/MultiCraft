package com.timkanake.multicraft;

import java.util.LinkedList;
import java.util.Queue;

public class CommandsQueue {
	Queue<String> commandsQ = new LinkedList<String>();
	
	private static CommandsQueue instance = null;
	private void CommandsQueue() {		
	}
	public static CommandsQueue getInstance() {
		if(instance == null) {
			instance = new CommandsQueue();
		}
		return instance;
	}
}
