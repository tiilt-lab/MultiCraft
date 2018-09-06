package com.timkanake.multicraft;


import java.io.*;
import java.net.*;
import com.timkanake.multicraft.EchoThread;

public class SpeechToTextServer {
	static final int PORT = 5001;
	public static void main(String[] args){
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(PORT);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				socket = serverSocket.accept();
			}catch(IOException e) {
				System.out.println("I/O Error " + e);
			}
			new EchoThread(socket).start();
		}
	}
}