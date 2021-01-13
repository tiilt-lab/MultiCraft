package com.multicraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class EchoThread extends Thread{
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
        while (true) {
            try {
                String line = brinp.readLine();
                if (line == null) {
                    socket.close();
                    return;
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
                                CommandsQueue.getInstance().commands.add(line);
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

