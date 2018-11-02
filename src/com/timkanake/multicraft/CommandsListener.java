package com.timkanake.multicraft;


import org.json.simple.JSONObject;

public class CommandsListener extends Thread{	
	MultiCraft plugin;
	
	public CommandsListener(MultiCraft pl) {
		plugin = pl;
	}
	
	public void run() {
		while(true) {
			if(! CommandsQueue.getInstance().commands.isEmpty()){
				String str = "[" + CommandsQueue.getInstance().commands.remove() + "]";
				JSONObject jsonObject = (JSONObject) JSONParsing.JSONFromString(str);
				CommandsQueue.getInstance().commandsQ.add(jsonObject);
			}
		}
	}
}
