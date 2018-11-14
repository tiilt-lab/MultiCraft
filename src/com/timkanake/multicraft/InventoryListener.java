package com.timkanake.multicraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;

public class InventoryListener implements Listener{

	private static Clock cl = Clock.systemDefaultZone();
	private static final int INVENTORY_OPEN_FLAG = 1;
	private static final int INVENTORY_CLOSE_FLAG = 0;
	
	
	@EventHandler
	public static void onInventoryOpen(InventoryOpenEvent event) throws SQLException {
		if(MySQL.isConnected()) {
			recordInventoryOpen(((Player) event.getPlayer()).getDisplayName());
		}
	}
	
	@EventHandler
	public static void onInventoryClose(InventoryCloseEvent event) throws SQLException {
		if(MySQL.isConnected()) {
			recordInventoryClose(((Player) event.getPlayer()).getDisplayName());
		}
	}

	private static void recordInventoryClose(String displayName) throws SQLException {
		recordInventoryEventUtility(displayName, InventoryListener.INVENTORY_CLOSE_FLAG);
		
	}

	
	private static void recordInventoryOpen(String displayName) throws SQLException {
		recordInventoryEventUtility(displayName, InventoryListener.INVENTORY_OPEN_FLAG);
	}
	
	
	private static void recordInventoryEventUtility(String displayName, int flag) throws SQLException {
		Connection c = MySQL.getConnection();
		int timeInMilliseconds = (int) cl.millis();
		PreparedStatement prepStatement = c.prepareStatement("insert into MultiCraft.Inventory_Events values (?, ?, ?)");
		prepStatement.setString(1,  displayName);
		prepStatement.setInt(2, timeInMilliseconds);
		prepStatement.setInt(3, flag);
	}

}
