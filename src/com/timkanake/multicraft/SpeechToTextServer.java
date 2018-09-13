package com.timkanake.multicraft;


import java.io.*;
import java.net.*;

import com.timkanake.multicraft.EchoThread;

public class SpeechToTextServer extends Thread{
	MultiCraft plugin;
	public SpeechToTextServer(MultiCraft pl) {
		plugin = pl;
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(5001);
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
	
//	public static void main(String[] args){
//		ServerSocket serverSocket = null;
//		Socket socket = null;
//		
//		try {
//			serverSocket = new ServerSocket(PORT);
//		}catch(IOException e) {
//			e.printStackTrace();
//		}
//		
//		while(true) {
//			try {
//				socket = serverSocket.accept();
//			}catch(IOException e) {
//				System.out.println("I/O Error " + e);
//			}
//			new EchoThread(socket).start();
//		}
//	}
}