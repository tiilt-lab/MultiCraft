package com.multicraft;

import org.json.simple.JSONObject;

/*
 * Gets processed JSON objects from the commands queue and creates a game command which is then executed.
 */
public class CommandExecution extends Thread{
	MultiCraft plugin;
	
	public CommandExecution(MultiCraft pl) {
		plugin = pl;
	}
	
	public void run() {
		while(true) {
			if(! CommandsQueue.getInstance().commandsQ.isEmpty()){
				try {
					JSONObject o = CommandsQueue.getInstance().commandsQ.remove();
					GameCommand gComm = new GameCommand(o, plugin);
					// TODO: Check if return value is false and indicate message
					gComm.execute();
				}catch(Exception e){
					// TODO: Write message to issuer
					plugin.getServer().broadcastMessage("Could not execute command");
				}				
			}
		}
	}
}
