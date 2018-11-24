package com.timkanake.multicraft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class MySQL {

    public static String host = "localhost";
    public static String port = "3306";
    public static String database = "MultiCraft";
    public static String username = "root";
    public static String password = "root";
    public static Connection con;

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    // connect
    public static void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                UtilityFunctions.printToConsole("MySQL connection was established");
//                console.sendMessage("\247c[\2476Minepedia-System\247c] \247bMySQL-A connection was established!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // disconnect
    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                UtilityFunctions.printToConsole("MySQL connection has been closed!");
//                console.sendMessage("\247c[\2476Minepedia-System\247c]\247bMySQL-Connection has been closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // isConnected
    public static boolean isConnected() {
        return (con == null ? false : true);
    }

    // getConnection
    public static Connection getConnection() {
        return con;
    }
}
