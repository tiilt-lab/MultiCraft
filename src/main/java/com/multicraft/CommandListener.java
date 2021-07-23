package com.multicraft;

import com.multicraft.data.CommandQueues;
import com.multicraft.util.JSONParser;
import com.multicraft.util.StoppableThread;
import org.json.simple.JSONObject;

/*
 * Consistently checks if any command requests have been received from a client and are yet to be processed.
 * If a command exists in the command queue instance, then the json string is retrieved and processed. 
 * Once processed then a command json object is added to a queue for final processing.
 */
public class CommandListener extends StoppableThread {

	private final MultiCraft plugin;

	public CommandListener(MultiCraft plugin) {
		this.plugin = plugin;
	}

	public void run() {
		while(!isStopped()) {
			if (CommandQueues.getInstance().containsStrings()) {
				String str = CommandQueues.getInstance().consumeString();
				JSONObject jsonObject = JSONParser.JSONFromString(str);
				if (jsonObject == null) {
					plugin.getLogger().warning(String.format("\"%s\" is not a valid JSON object.", str));
				} else {
					CommandQueues.getInstance().addObject(jsonObject);
				}
			}
		}
	}

}
