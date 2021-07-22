package com.multicraft.net;

import com.multicraft.MultiCraft;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileThread extends Thread{
	protected Socket socket;
	MultiCraft plugin;
	private final String jarLocation;
	public FileThread(Socket clientSocket, MultiCraft pl) {
		socket = clientSocket;
		plugin = pl;
		File filePath = new File(FileThread.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
	}

	public void run() {
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			File f = new File(jarLocation + "\\MultiCraft\\" + socket.getInetAddress().getHostAddress() + "Gaze.csv");
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f, true);
			while (!socket.isClosed()) {
				byte[] b = new byte[dis.available()];
				dis.readFully(b);
				fos.write(b);
			}
			dis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

