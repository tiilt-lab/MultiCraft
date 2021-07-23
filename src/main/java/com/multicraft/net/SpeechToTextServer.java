package com.multicraft.net;

import com.multicraft.data.CommandQueues;
import com.multicraft.MultiCraft;
import com.multicraft.util.StoppableThread;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/*
 * This Server handles socket connections for each client. One server receives connection requests and for each, 
 * creates a unique EchoThread instance to communicate with that particular client
 */

public class SpeechToTextServer extends StoppableThread {
	
	MultiCraft plugin;
	
	public SpeechToTextServer(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("resource")
	public void run() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(5003);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		while(!isStopped()) {
			try {
				socket = serverSocket.accept();
			}catch(IOException e) {
				e.printStackTrace();
			}
			new EchoThread(socket, plugin).start();
		}
	}

	public static class EchoThread extends Thread {

		protected Socket socket;
		MultiCraft plugin;

		public EchoThread(Socket clientSocket, MultiCraft pl) {
			socket = clientSocket;
			plugin = pl;
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
}