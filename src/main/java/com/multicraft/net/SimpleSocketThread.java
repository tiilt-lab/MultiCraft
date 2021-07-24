package com.multicraft.net;

import com.multicraft.MultiCraft;

import java.net.Socket;

public class SimpleSocketThread extends Thread {

    protected Socket socket;
    protected MultiCraft plugin;

    public SimpleSocketThread(Socket socket, MultiCraft plugin) {
        this.socket = socket;
        this.plugin = plugin;
    }

    public interface Factory<T extends SimpleSocketThread> {
        T get(Socket socket, MultiCraft plugin);
    }
}
