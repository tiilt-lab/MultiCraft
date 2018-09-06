package com.timkanake.multicraft;

import java.util.LinkedList;
import java.util.Queue;
import com.timkanake.multicraft.GameCommand;

public class CommandsListener{
	CommandsQueue cQ = CommandsQueue.getInstance();
	CommandWords cW = CommandWords.getInstance();
	public void handleCommands() {
		while(true) {
			while(! cQ.commandsQ.isEmpty()) {
				executeCommand(cQ.commandsQ.poll());
			}
		}
	}
	
	public void executeCommand(String command) {
		GameCommand gameCommand = new GameCommand(command);
		gameCommand.execute();
	}
}
