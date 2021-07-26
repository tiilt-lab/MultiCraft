package com.multicraft.net;

import com.multicraft.MultiCraft;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;


public class FileThread extends SimpleSocketThread {

    public FileThread(Socket socket, MultiCraft plugin) {
        super(socket, plugin);
    }

    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            File f = new File(plugin.MultiCraftDirName + File.separator + socket.getInetAddress().getHostAddress() + "Gaze.csv");
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
            plugin.getLogger().warning(String.format("IOException: \"%s\"", e.getMessage()));
        }
    }

}
