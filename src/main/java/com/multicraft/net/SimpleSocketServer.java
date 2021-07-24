package com.multicraft.net;

import com.multicraft.MultiCraft;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleSocketServer<T extends SimpleSocketThread> extends Thread {

    private final MultiCraft plugin;
    private final int port;
    private final SimpleSocketThread.Factory<T> threadFactory;

    public SimpleSocketServer(MultiCraft plugin, int port, SimpleSocketThread.Factory<T> threadFactory) {
        this.plugin = plugin;
        this.port = port;
        this.threadFactory = threadFactory;
    }

    @SuppressWarnings("resource")
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (!Thread.interrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    threadFactory.get(socket, plugin).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
