package com.multicraft.data;

import com.multicraft.GameCommand;
import com.multicraft.MultiCraft;
import com.multicraft.util.JSONParser;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandQueues {

	private final Queue<JSONObject> jsonQueue = new ConcurrentLinkedQueue<>();
	private final Queue<String> strQueue = new ConcurrentLinkedQueue<>();

	private static CommandQueues instance = null;
	
	public static CommandQueues getInstance() {
		if (instance == null) {
			instance = new CommandQueues();
		}
		return instance;
	}

	public void addString(String command) {
		strQueue.add(command);
	}

	public String consumeString() {
		return strQueue.remove();
	}

	public boolean containsStrings() {
		return !strQueue.isEmpty();
	}

	public void addObject(JSONObject command) {
		jsonQueue.add(command);
	}

	public JSONObject consumeObject() {
		return jsonQueue.remove();
	}

	public boolean containsObjects() {
		return !jsonQueue.isEmpty();
	}

	/*
	 * Consistently checks if any command requests have been received from a client and are yet to be processed.
	 * If a command exists in the command queue instance, then the json string is retrieved and processed.
	 * Once processed then a command json object is added to a queue for final processing.
	 */
	public static class CommandListener extends Thread {

		private final MultiCraft plugin;

		public CommandListener(MultiCraft plugin) {
			this.plugin = plugin;
		}

		public void run() {
			while(!Thread.interrupted()) {
				if (getInstance().containsStrings()) {
					String str = getInstance().consumeString();
					JSONObject jsonObject = JSONParser.JSONFromString(str);
					if (jsonObject == null) {
						plugin.getLogger().warning(String.format("\"%s\" is not a valid JSON object.", str));
					} else {
						getInstance().addObject(jsonObject);
					}
				}
			}
		}

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
					try {
						JSONObject o = getInstance().consumeObject();
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

}
