package com.multicraft;

import org.bukkit.Location;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CoordinatesCalculator {
	
	public static int[] getBuildCoordinates(Location playerloc, Location loc, int[] dimensions) {
		int angle = (int) playerloc.getYaw();

		int startX = loc.getBlockX(), startY = loc.getBlockY(), startZ = loc.getBlockZ();

		String generalDirection = getGeneralDirection(angle);

		switch (generalDirection) {
			case "north":
				return getFacingNorthCoordinates(startX, startY, startZ, dimensions);
			case "east":
				return getFacingEastCoordinates(startX, startY, startZ, dimensions);
			case "south":
				return getFacingSouthCoordinates(startX, startY, startZ, dimensions);
			default:
				return getFacingWestCoordinates(startX, startY, startZ, dimensions);
		}
	}
	
	private static int[] getFacingNorthCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endZ = startZ - dimensions[0] + 1;
		int endY = startY + dimensions[1] - 1;
		int endX = startX - dimensions[2] + 1;
		return new int[] {endX, endY, endZ};
	}

	private static int[] getFacingEastCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endZ = startZ - dimensions[2] + 1;
		int endY = startY + dimensions[1] - 1;
		int endX = startX + dimensions[0] - 1;
		return new int[] {endX, endY, endZ};
	}

	private static int[] getFacingSouthCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endZ = startZ + dimensions[0] - 1;
		int endY = startY + dimensions[1] - 1;
		int endX = startX + dimensions[2] - 1;
		return new int[] {endX, endY, endZ};
	}

	private static int[] getFacingWestCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endZ = startZ + dimensions[2] - 1;
		int endY = startY + dimensions[1] - 1;
		int endX = startX - dimensions[0] + 1;
		return new int[] {endX, endY, endZ};
	}

	public static String getGeneralDirection(int angle) {
		String specificDirection = getSpecificDirection(angle);
		Set<String> northDir = new HashSet<>(Arrays.asList("north northwest", "north", "north northeast", "northeast"));
		Set<String> eastDir = new HashSet<>(Arrays.asList("east northeast", "east", "east southeast", "southeast"));
		Set<String> southDir = new HashSet<>(Arrays.asList("south southeast", "south", "south southwest", "southwest"));
//		Set<String> westDir = new HashSet<String>(Arrays.asList("west southwest", "west", "west northwest", "northwest"));
		
		if (northDir.contains(specificDirection))
			return "north";
		if (eastDir.contains(specificDirection))
			return "east";
		if (southDir.contains(specificDirection))
			return "south";
		return "west";
	}
	
	private static String getSpecificDirection(int angle) {
		if (angle < 0)
			angle += 360;

		int dirInt = (int) ((angle + 8) / 22.5);

		switch (dirInt) {
			case 1: return "south southwest";
			case 2: return "southwest";
			case 3: return "west southwest";
			case 4: return "west";
			case 5: return "west northwest";
			case 6: return "northwest";
			case 7: return "north northwest";
			case 8: return "north";
			case 9: return "north northeast";
			case 10: return "northeast";
			case 11: return "east northeast";
			case 12: return "east";
			case 13: return "east southeast";
			case 14: return "southeast";
			case 15: return "south southeast";
			default: return "south";
		}
	}

}
