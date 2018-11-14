package com.timkanake.multicraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;
import java.util.ArrayList;
import com.timkanake.multicraft.MySQL;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LocationsListener implements Listener{
	private ArrayList<Player> players = new ArrayList<Player>();
	private Clock cl = Clock.systemDefaultZone();
	MultiCraft plugin;
	public LocationsListener(MultiCraft pl) {
		plugin = pl;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		players.add(event.getPlayer());
		if(MySQL.isConnected()) {
			Player p = event.getPlayer();
			p.sendMessage("Database is connected");
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player pl = event.getPlayer();
		if(players.contains(pl)) {
			players.remove(pl);
		}
	}
	
	@EventHandler
	public void onPlayerKickedOut(PlayerKickEvent event) {
		Player pl = event.getPlayer();
		if(players.contains(pl)) {
			players.remove(pl);
		}
	}
	
	@EventHandler
	public void onPlayerMovement(PlayerMoveEvent event) throws SQLException {
		Player pl = event.getPlayer();
		Location prevLocation = event.getFrom();
		Location newLocation = event.getTo();
		if(movementMagnitude(prevLocation, newLocation) >= 1) {
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
