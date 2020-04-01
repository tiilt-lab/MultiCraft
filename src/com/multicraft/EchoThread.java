package com.multicraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.multicraft.CommandsQueue;

public class EchoThread extends Thread{
	protected Socket socket;
	CommandsQueue cQ = CommandsQueue.getInstance();
	MultiCraft plugin;
	public EchoThread(Socket clientSocket, MultiCraft pl) {
		socket = clientSocket;
		plugin = pl;
	}
	
	public void run() {
		InputStream inp = null;
        BufferedReader brinp = null;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
        } catch (IOException e) {
            return;
        }
        String line;
        while (true) {
            try {
                line = brinp.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                	CommandsQueue.getInstance().commands.add(line);

                    String clientNameField = "\"client_name\": \"";
                    int start = line.indexOf(clientNameField) + clientNameField.length();
                    plugin.getServer().getPlayer(java.util.UUID.fromString(line.substring(start, start + 36))).sendMessage(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}

