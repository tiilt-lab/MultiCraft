package com.multicraft;

import java.util.Arrays;
import java.util.HashSet;

public class CommandWords {

	private static CommandWords instance = null;
	HashSet<String> commands = new HashSet<>();

	private CommandWords() {
		String[] comms = {"build", "place", "move", "turn", "tilt", "undo", "redo", "store", "clone", "give", "tbuild"};
		commands.addAll(Arrays.asList(comms));
	}
	
	public static CommandWords getInstance() {
		if(instance == null) {
			instance = new CommandWords();
		}
		return instance;
	}

}
