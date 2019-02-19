package com.timkanake.multicraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;
import java.util.HashMap;

import com.timkanake.multicraft.MySQL;
import com.timkanake.multicraft.UtilityFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LocationsListener implements Listener{
	private HashMap<String, Integer> playerLastRecordedTime = new HashMap<String, Integer>();
	private Clock cl = Clock.systemDefaultZone();
	MultiCraft plugin;
	static ConsoleCommandSender console = Bukkit.getConsoleSender();
	
	public LocationsListener(MultiCraft pl) {
		plugin = pl;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			Player p = event.getPlayer();
			if(!playerLastRecordedTime.containsKey(p.getDisplayName())) {
				playerLastRecordedTime.put(p.getDisplayName(), Integer.MIN_VALUE);
			}
			if(MySQL.isConnected()) {			
				p.sendMessage("Database is connected");
			}
		}catch(Exception e) {
			return;
		}
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {	
		removePlayerFromTimeRecordedMap(event.getPlayer().getDisplayName());
	}
	
	public void removePlayerFromTimeRecordedMap(String displayName) {
		try {
			playerLastRecordedTime.remove(displayName);
		}catch(Exception e) {
			UtilityFunctions.printToConsole("Tried to remove player from locations map but name not found");
			return;
		}
	}
	
	@EventHandler
	public void onPlayerKickedOut(PlayerKickEvent event) {
		removePlayerFromTimeRecordedMap(event.getPlayer().getDisplayName());
	}
	
	@EventHandler
	public void onPlayerMovement(PlayerMoveEvent event) throws SQLException {
		Player pl = event.getPlayer();
		Location prevLocation = event.getFrom();
		Location newLocation = event.getTo();
		int curTime = (int) cl.millis();
//		int timeDiff = curTime - this.playerLastRecordedTime.get(pl.getDisplayName());
		if(movementMagnitude(prevLocation, newLocation) >= 1) {
			this.playerLastRecordedTime.put(pl.getDisplayName(), curTime);
			recordLocation(newLocation, pl.getDisplayName());
		}		
	}
	
	private int movementMagnitude(Location loc1, Location loc2) {
		double xMag = Math.pow((loc2.getX() - loc1.getX()), 2);
		double yMag = Math.pow((loc2.getY() - loc1.getY()),2);
		double zMag = Math.pow((loc2.getZ() - loc1.getZ()),2);
		
		return (int) Math.ceil(Math.sqrt(xMag + yMag + zMag));
	}
	
	private void recordLocation(Location loc, String playerName) throws SQLException {
		int timeInMilliseconds = (int) cl.millis();
		Connection c = MySQL.getConnection();
		PreparedStatement prepStatement = c.prepareStatement("insert into MultiCraft.Player_Locations values (?, ?, ?, ?, ?)");
		
		prepStatement.setString(1, playerName);
		prepStatement.setInt(2, timeInMilliseconds);
		prepStatement.setDouble(3,  loc.getX());
		prepStatement.setDouble(4, loc.getY());
		prepStatement.setDouble(5, loc.getZ());
		
		prepStatement.executeUpdate();
	}
}
