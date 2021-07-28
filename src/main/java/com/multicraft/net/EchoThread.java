package com.multicraft.net;

import com.multicraft.data.CommandQueue;
import com.multicraft.MultiCraft;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.UUID;


public class EchoThread extends SimpleSocketThread {

	private final JSONParser jsonParser = new JSONParser();
	private String client = "";

	public EchoThread(Socket socket, MultiCraft plugin) {
	    super(socket, plugin);
	}

	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while (!socket.isClosed() && (line = bufferedReader.readLine()) != null) {
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
					client = (String) jsonObject.get("client_name");
					if (client != null && !client.isEmpty()) {
						Player player = plugin.getServer().getPlayer(UUID.fromString(client));
						if (player != null) {
							player.sendMessage(line);
							CommandQueue.getInstance().addObject(jsonObject);
							plugin.getLogger().info(line);
						}
					}
				} catch (IllegalArgumentException e) {
					plugin.getLogger().warning(String.format("IllegalArgumentException: \"%s\" is an invalid UUID.", client));
				} catch (ParseException e) {
					plugin.getLogger().warning(String.format("ParseException: Position %s of \"%s\".", e.getPosition(), line));
				}
			}
		} catch (IOException e) {
			plugin.getLogger().severe(String.format("IOException: \"%s\".", e.getMessage()));
		}
	}

}