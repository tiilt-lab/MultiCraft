package com.multicraft;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RegionBuild {
	private static HashMap<Player, RegionCoordinates> playerRegionSelections = new HashMap<Player, RegionCoordinates>();
	private static RegionBuild instance = null;
	
	
	public static RegionBuild getInstance() {
		if(instance == null) {
			instance = new RegionBuild();
		}
		return instance;
	}
	
	private RegionBuild() {
	}
	
	
	public void startRegionBuildForPlayer(Player p) {
		RegionCoordinates r = new RegionCoordinates();
		playerRegionSelections.put(p, r);
	}
	
	
	public void markStartPosition(Player p, Location l) {
		if(playerRegionSelections.containsKey(p)){
			RegionCoordinates rCoords = playerRegionSelections.get(p);
			rCoords.setStartLocation(l);
			playerRegionSelections.put(p, rCoords);
			return;
		}
		
		startRegionBuildForPlayer(p);
		markStartPosition(p, l);
		return;
	}
	
	public boolean markEndPosition(Player p, Location l) {
		if(! playerRegionSelections.containsKey(p))
			return false;
		
		RegionCoordinates rCoords = playerRegionSelections.get(p);
		rCoords.setEndLocation(l);
		playerRegionSelections.put(p, rCoords);
		return true;
		
	}
	
	public Location getStartLocation(Player p) {
		if(! playerRegionSelections.containsKey(p)) {
			// TODO: Throw Exception
			return null;
		}
		
		RegionCoordinates rCoords = playerRegionSelections.get(p);
		return rCoords.startLocation;
	}
	
	public Location getEndLocation(Player p) {
		if(! playerRegionSelections.containsKey(p)) {
			// TODO: Throw Exception
			return null;
		}
		
		RegionCoordinates rCoords = playerRegionSelections.get(p);
		return rCoords.endLocation;
	}

	
	private class RegionCoordinates{
		private Location startLocation;
		private Location endLocation;
		
		public RegionCoordinates() {
			startLocation = null;
			endLocation = null;
		}
		
//		public boolean hasBothCoordinates() {
//			return startLocation != null && endLocation != null;
//		}
		
		public void setStartLocation(Location loc) {
			startLocation = loc;
		}
		
		public void setEndLocation(Location loc) {
			endLocation = loc;
		}
	}
}
