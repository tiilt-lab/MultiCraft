package com.timkanake.multicraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public class BlockEventsListener implements Listener{
	MultiCraft plugin;
	static ConsoleCommandSender console = Bukkit.getConsoleSender();
	private static Clock cl = Clock.systemDefaultZone();
	private static final int BLOCK_BREAK_FLAG = 0;
	private static final int BLOCK_PLACE_FLAG = 1;
	
	public BlockEventsListener(MultiCraft pl) {		
		plugin = pl;
		UtilityFunctions.printToConsole("Block Placement Listener Initialized");
	}
	
	
	@EventHandler
	public static void onBlockPlaceEvent(BlockPlaceEvent event) throws SQLException {
		if(! MySQL.isConnected())
			return;
		String playerName = event.getPlayer().getDisplayName();
		Location blockLocation = event.getBlock().getLocation();
		
		recordBlockEvent(playerName, blockLocation, BLOCK_PLACE_FLAG);
	}
	
	
	@EventHandler
	public static void onBlockBreakEvent(BlockBreakEvent event) throws SQLException{
		if (! MySQL.isConnected())
			return;
		String playerName = event.getPlayer().getDisplayName();
		Location blockLocation = event.getBlock().getLocation();
		
		recordBlockEvent(playerName, blockLocation, BLOCK_BREAK_FLAG);
	}
	
	public static void recordBlockEvent(String playerName, Location  blockLocation, int eventFlag) throws SQLException {
		Connection  c = MySQL.getConnection();
		int timeInMilliseconds = (int) cl.millis();
		PreparedStatement prepStatement = c.prepareStatement("insert into MultiCraft.block_events values (?, ?, ?, ?, ?, ?)");
		prepStatement.setString(1, playerName);
		prepStatement.setInt(2,  timeInMilliseconds);
		prepStatement.setDouble(3, blockLocation.getX());
		prepStatement.setDouble(4, blockLocation.getY());
		prepStatement.setDouble(5, blockLocation.getZ());
		prepStatement.setInt(6,  eventFlag);
		
		prepStatement.executeUpdate();
	}
}
