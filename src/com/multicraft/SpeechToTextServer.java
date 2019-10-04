package com.multicraft;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.multicraft.EchoThread;

/*
 * This Server handles socket connections for each client. One server receives connection requests and for each, 
 * creates a unique EchoThread instance to communicate with that particular client
 */

public class SpeechToTextServer extends Thread{
	
	MultiCraft plugin;
	
	public SpeechToTextServer(MultiCraft pl) {
		plugin = pl;
	}
	
	@SuppressWarnings("resource")
	public void run() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(5003);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				socket = serverSocket.accept();
			}catch(IOException e) {
			}
			new EchoThread(socket, plugin).start();
		}
	}
}