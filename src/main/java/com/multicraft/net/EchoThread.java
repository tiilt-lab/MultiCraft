package com.multicraft.net;

import com.multicraft.data.CommandQueues;
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

	public EchoThread(Socket socket, MultiCraft plugin) {
	    super(socket, plugin);
	}

	public void run() {
		InputStream inp;
		BufferedReader brinp;
		try {
			inp = socket.getInputStream();
			brinp = new BufferedReader(new InputStreamReader(inp));
		} catch (IOException e) {
			return;
		}
		while (!socket.isClosed()) {
			try {
				String line = brinp.readLine();
				if (line == null) {
					socket.close();
					break;
				}
				try {
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(line);
					Object client = obj.get("client_name");
					if (client != null) {
						try {
							Player player = plugin.getServer().getPlayer(UUID.fromString(client.toString()));
							if (player != null) {
								player.sendMessage(line);
								CommandQueues.getInstance().addString(line);
								System.out.println(line);
							}
						} catch (IllegalArgumentException ie) {
							ie.printStackTrace();
						}
					}
				} catch(ParseException pe) {
					System.out.println("position: " + pe.getPosition());
					pe.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}