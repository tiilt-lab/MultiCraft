package com.timkanake.multicraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import com.timkanake.multicraft.CommandsQueue;

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
                	cQ.commandsQ.add(line);
                	plugin.getServer().broadcastMessage(line);
                	// System.out.println("RECIEVED :" + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}

