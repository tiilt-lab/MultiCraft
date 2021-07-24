package com.multicraft;

import com.multicraft.data.RegionCoordinates;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RegionSelector {

	protected static HashMap<Player, RegionCoordinates> playerRegionSelections = new HashMap<>();

	private static RegionSelector instance = null;

	public static RegionSelector getInstance() {
		if(instance == null) {
			instance = new RegionSelector();
		}
		return instance;
	}
	
	public RegionSelector() { }

	public void startRegionSelectForPlayer(Player p) {
		RegionCoordinates r = new RegionCoordinates();
		playerRegionSelections.put(p, r);
	}
	
	
	public void markStartPosition(Player p, Location l) {
		if (playerRegionSelections.containsKey(p)){
			RegionCoordinates rCoords = playerRegionSelections.get(p);
			rCoords.setStartLocation(l);
			playerRegionSelections.put(p, rCoords);
			return;
		}
		
		startRegionSelectForPlayer(p);
		markStartPosition(p, l);
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
		if (!playerRegionSelections.containsKey(p)) {
			// TODO: Throw Exception
			return null;
		}
		
		RegionCoordinates rCoords = playerRegionSelections.get(p);
		return rCoords.getStartLocation();
	}
	
	public Location getEndLocation(Player p) {
		if (!playerRegionSelections.containsKey(p)) {
			// TODO: Throw Exception
			return null;
		}
		
		RegionCoordinates rCoords = playerRegionSelections.get(p);
		return rCoords.getEndLocation();
	}

}
