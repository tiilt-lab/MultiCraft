package com.timkanake.multicraft;

import java.time.Clock;
import java.util.ArrayList;


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
	public LocationsListener(MultiCraft plugin) {
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		players.add(event.getPlayer());
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
	public void onPlayerMovement(PlayerMoveEvent event) {
		Player pl = event.getPlayer();
		Location prevLocation = event.getFrom();
		Location newLocation = event.getTo();
		if(movementMagnitude(prevLocation, newLocation) >= 1) {
			long secs = recordLocation(newLocation, pl.getDisplayName());
			pl.sendMessage("You moved at " + Long.toString(secs));
		}
		
	}
	
	private int movementMagnitude(Location loc1, Location loc2) {
		double xMag = Math.pow((loc2.getX() - loc1.getX()), 2);
		double yMag = Math.pow((loc2.getY() - loc1.getY()),2);
		double zMag = Math.pow((loc2.getZ() - loc1.getZ()),2);
		
		return (int) Math.ceil(Math.sqrt(xMag + yMag + zMag));
	}
	
	private long recordLocation(Location loc, String playerName) {
		long timeInMilliseconds = cl.millis();
		return timeInMilliseconds;
	}
}
