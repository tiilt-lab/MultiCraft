package com.multicraft;

import java.util.UUID;

import org.bukkit.entity.Player;
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
			if (! CommandsQueue.getInstance().commandsQ.isEmpty()) {
				try {
					JSONObject o = CommandsQueue.getInstance().commandsQ.remove();
					GameCommand gComm = new GameCommand(o, plugin);

					if (! gComm.execute()) {
						String client = o.get("client_name").toString();
						Player issuer = plugin.getServer().getPlayer(UUID.fromString(client));
						if (issuer != null)
							issuer.sendMessage("Command execution failed.");
					}

				} catch (Exception e) {
					// TODO: Write message to issuer
					e.printStackTrace();
					plugin.getServer().broadcastMessage("Could not execute incoming command.");
				}				
			}
		}
	}
}
