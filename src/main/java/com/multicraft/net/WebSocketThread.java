package com.multicraft.net;

import com.multicraft.MultiCraft;
import com.multicraft.data.CommandQueue;
import com.multicraft.util.UUIDParser;
import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class WebSocketThread extends WebSocketServer {

    private static final String MINECRAFT_API = "https://api.mojang.com/users/profiles/minecraft/";

    private final MultiCraft plugin;
    private final JSONParser jsonParser = new JSONParser();

    /* Only to be used by local main method for testing */
    private WebSocketThread(int port) {
        super(new InetSocketAddress(port));
        plugin = null;
    }

    public WebSocketThread(MultiCraft plugin, int port) {
        super(new InetSocketAddress(port));
        this.plugin = plugin;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logInfo(String.format("New connection from %s.", conn.getRemoteSocketAddress()));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logInfo(String.format("Closed %s with exit code %d. Additional Info: %s", conn.getRemoteSocketAddress(), code, reason));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logInfo(String.format("Received message from %s: %s", conn.getRemoteSocketAddress(), message));
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);
            String client = (String) jsonObject.get("client_name");
            if (plugin != null && client != null && !client.isEmpty()) {
                /* Check for and handle incoming commands. Only possible if server is running in plugin context. */
                Player player = plugin.getServer().getPlayer(UUIDParser.parse(client));
                if (player != null) {
                    player.sendMessage(message);
                    CommandQueue.getInstance().addObject(jsonObject);
                    conn.send("{type: info, message: \"Command received.\"");
                }
            } else {
                /* JS clients may be attempting to login to MultiCraft. Check for and handle "login" command. */
                String command = (String) jsonObject.get("command");
                String username = (String) jsonObject.get("username");
                if (command != null && command.equals("login") && username != null && !username.isEmpty()) {
                    URL url = new URL(MINECRAFT_API + username);
                    InputStream inputStream = url.openStream();
                    String result = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    inputStream.close();
                    conn.send(result);
                    logInfo(result);
                }
            }
        } catch (ParseException e) {
            logWarning(String.format("ParseException: Position %s of %s", e.getPosition(), message));
        } catch (MalformedURLException e) {
            logWarning(String.format("MalformedURLException: \"%s\"", e.getMessage()));
        } catch (IOException e) {
            logWarning(String.format("IOException: \"%s\"", e.getMessage()));
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        logInfo(String.format("Received ByteBuffer from %s: %s", conn.getRemoteSocketAddress(), message));
        onMessage(conn, StandardCharsets.UTF_8.decode(message).toString());
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        logInfo(String.format("An error occurred on connection %s: %s", conn.getRemoteSocketAddress(), e));
    }

    @Override
    public void onStart() {
        logInfo("WebSocketServer started successfully.");
    }

    private void logInfo(String message) {
        if (plugin != null) {
            plugin.getLogger().info(message);
        } else {
            System.out.println(message);
        }
    }

    private void logWarning(String message) {
        if (plugin != null) {
            plugin.getLogger().warning(message);
        } else {
            System.err.println(message);
        }
    }

    public static void main(String[] args) {
        WebSocketServer server = new WebSocketThread(5005);
        server.run();
    }

}