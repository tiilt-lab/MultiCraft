package com.multicraft;

import org.json.simple.JSONObject;

/*
 * Consistently checks if any command requests have been received from a client and are yet to be processed.
 * If a command exists in the command queue instance, then the json string is retrieved and processed. 
 * Once processed then a command json object is added to a queue for final processing.
 */
public class CommandsListener extends Thread{	
	MultiCraft plugin;
	
	public CommandsListener(MultiCraft pl) {
		plugin = pl;
	}
	
	public void run() {
		while(true) {
			if(! CommandsQueue.getInstance().commands.isEmpty()){
				String str = "[" + CommandsQueue.getInstance().commands.remove() + "]";
				System.out.println(str);
				JSONObject jsonObject = (JSONObject) JSONParsing.JSONFromString(str);
				CommandsQueue.getInstance().commandsQ.add(jsonObject);
			}
		}
	}
}
