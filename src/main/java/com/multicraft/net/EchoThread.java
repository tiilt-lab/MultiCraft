package com.multicraft.net;

import com.multicraft.data.CommandQueue;
import com.multicraft.MultiCraft;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;


public class EchoThread extends SimpleSocketThread {

	private final JSONParser jsonParser = new JSONParser();
	private String client = "";

	public EchoThread(Socket socket, MultiCraft plugin) {
	    super(socket, plugin);
	}

	public void run() {
		InputStream inputStream;
		try {
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			plugin.getLogger().severe(String.format("IOException: \"%s\"", e.getMessage()));
			return;
		}

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		while (!socket.isClosed()) {
			String line = "";
			try {
				line = bufferedReader.readLine();
				if (line == null || line.isEmpty()) {
					socket.close();
					break;
				}
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
			} catch (IOException e) {
				plugin.getLogger().warning(String.format("IOException: \"%s\"", e.getMessage()));
			} catch (IllegalArgumentException e) {
				plugin.getLogger().warning(String.format("IllegalArgumentException: \"%s\" is an invalid UUID.", client));
			} catch (ParseException e) {
				plugin.getLogger().warning(String.format("ParseException: Position %s of ", e.getPosition(), line));
			}
		}
	}

}