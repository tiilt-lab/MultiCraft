package com.timkanake.multicraft;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;

public class InventoryListener implements Listener{

	private static Clock cl = Clock.systemDefaultZone();
	private static final int INVENTORY_CLOSE_FLAG = 0;
	private static final int INVENTORY_CLICK_FLAG = 1;
	
	
	static ConsoleCommandSender console = Bukkit.getConsoleSender();
	MultiCraft plugin;
	public InventoryListener(MultiCraft pl) {		
		plugin = pl;
		UtilityFunctions.printToConsole("Inventory Listener Initialized");
	}
	
	@EventHandler
	public static void onInventoryClick(InventoryClickEvent event) throws SQLException {
		if(MySQL.isConnected()) {
//			String playerName = event.getWhoClicked().getName();
			recordInventoryClick(event.getWhoClicked().getName());
		}
	}
	
	@EventHandler
	public static void onInventoryClose(InventoryCloseEvent event) throws SQLException {
		if(MySQL.isConnected()) {
			recordInventoryClose(event.getPlayer().getName());
		}
	}

	private static void recordInventoryClose(String displayName) throws SQLException {
		recordInventoryEventUtility(displayName, InventoryListener.INVENTORY_CLOSE_FLAG);
		
	}

	
	private static void recordInventoryClick(String displayName) throws SQLException {
		recordInventoryEventUtility(displayName, InventoryListener.INVENTORY_CLICK_FLAG);
	}
	
	
	private static void recordInventoryEventUtility(String displayName, int flag) throws SQLException {
		Connection c = MySQL.getConnection();
		int timeInMilliseconds = (int) cl.millis();
		PreparedStatement prepStatement = c.prepareStatement("insert into multiCraft.invetory_events values (?, ?, ?)");
		prepStatement.setString(1,  displayName);
		prepStatement.setInt(2, timeInMilliseconds);
		prepStatement.setInt(3, flag);
		prepStatement.executeUpdate();
	}
}
