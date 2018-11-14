package com.timkanake.multicraft;

import org.json.simple.JSONObject;

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
					plugin.getServer().broadcastMessage(o.toJSONString() + " this is right");
					GameCommand gComm = new GameCommand(o, plugin);
					gComm.execute();
				}catch(Exception e){
					plugin.getServer().broadcastMessage("Could not execute command");
				}				
			}
		}
	}
}
