package com.multicraft;

import java.util.HashSet;

public class CommandWords {
	private static CommandWords instance = null;
	HashSet<String> commands = new HashSet<String>();
	private CommandWords() {
		String[] comms = {"build", "move", "redo", "undo", "duplicate", "save", "teleport"};
		for(String s: comms) {
			commands.add(s);
		}		
	}
	
	public static CommandWords getInstance() {
		if(instance == null) {
			instance = new CommandWords();
		}
		return instance;
	}
}
