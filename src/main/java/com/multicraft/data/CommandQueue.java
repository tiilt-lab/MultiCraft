package com.multicraft.data;

import com.multicraft.GameCommand;
import com.multicraft.MultiCraft;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandQueue {

	private final Queue<JSONObject> queue = new ConcurrentLinkedQueue<>();

	private static CommandQueue instance = null;
	
	public static CommandQueue getInstance() {
		if (instance == null) {
			instance = new CommandQueue();
		}
		return instance;
	}

	public void addObject(JSONObject command) {
		queue.add(command);
	}

	public JSONObject consumeObject() {
		return queue.remove();
	}

	public boolean containsObjects() {
		return !queue.isEmpty();
	}

	/*
	 * Gets processed JSON objects from the commands queue and creates a game command which is then executed.
	 */
	public static class CommandExecutor extends Thread {

		private final MultiCraft plugin;

		public CommandExecutor(MultiCraft plugin) {
			this.plugin = plugin;
		}

		public void run() {
			while(!Thread.interrupted()) {
				if (getInstance().containsObjects()) {
					JSONObject jsonObject = getInstance().consumeObject();
					GameCommand gComm = new GameCommand(jsonObject, plugin);

					if (gComm.execute()) {
						continue;
					}

					String client = (String) jsonObject.get("client_name");
					Player issuer = plugin.getServer().getPlayer(UUID.fromString(client));
					if (issuer != null) {
						issuer.sendMessage(String.format("\"%s\" command execution failed.", jsonObject.get("command")));
					}
				}
			}
		}

	}

}
