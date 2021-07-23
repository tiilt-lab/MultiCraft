package com.multicraft;

import com.multicraft.data.CommandQueues;
import com.multicraft.util.StoppableThread;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;

/*
 * Gets processed JSON objects from the commands queue and creates a game command which is then executed.
 */
public class CommandExecutor extends StoppableThread {

	MultiCraft plugin;
	
	public CommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		while(!isStopped()) {
			if (CommandQueues.getInstance().containsObjects()) {
				try {
					JSONObject o = CommandQueues.getInstance().consumeObject();
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
