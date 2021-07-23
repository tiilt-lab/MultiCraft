package com.multicraft.net;

import com.multicraft.MultiCraft;
import com.multicraft.util.StoppableThread;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/*
 * This Server handles socket connections for each client. One server receives connection requests and for each,
 * creates a unique EchoThread instance to communicate with that particular client
 */

public class FileTransferServer extends StoppableThread {

	private MultiCraft plugin;

	public FileTransferServer(MultiCraft plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("resource")
	public void run() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(5004);
		} catch(IOException e) {
			e.printStackTrace();
		}

		while(!isStopped()) {
			try {
                socket = serverSocket.accept();
			}catch(IOException e) {
				e.printStackTrace();
			}
			new FileThread(socket).start();
		}
	}

    public static class FileThread extends Thread{

        private final Socket clientSocket;
        private final String jarLocation;

        public FileThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            File filePath = new File(FileThread.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            jarLocation = filePath.getPath().substring(0, filePath.getPath().indexOf(filePath.getName()));
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                File f = new File(jarLocation + "\\MultiCraft\\" + clientSocket.getInetAddress().getHostAddress() + "Gaze.csv");
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(f, true);
                while (!clientSocket.isClosed()) {
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
}
