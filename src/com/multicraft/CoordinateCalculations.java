package com.multicraft;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

public class CoordinateCalculations {
	
	public static int[] getBuildCoordinates(Location loc, int[] dimensions) {
		int angle = (int) loc.getYaw();
		int startX = loc.getBlockX(), startY = loc.getBlockY(), startZ = loc.getBlockZ();
		String generalDirection = getGeneralDirection(angle);
		int[] dirs = new int[3];
		if(generalDirection.equals("north")) {
			dirs = getFacingNorthCoordinates(startX, startY, startZ, dimensions);
		}else if(generalDirection.equals("east")) {
			dirs = getFacingEastCoordinates(startX, startY, startZ, dimensions);
		}else if(generalDirection.equals("south")) {
			dirs = getFacingSouthCoordinates(startX, startY, startZ, dimensions);
		}else {
			dirs = getFacingWestCoordinates(startX, startY, startZ, dimensions);
		}
		return new int[] {dirs[0], dirs[1], dirs[2]};
	}
	
	private static int[] getFacingNorthCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endX = 0, endZ = 0, endY = 0;
		endZ = startZ - dimensions[0] + 1;
		endY = startY + dimensions[1] - 1;
		endX = startX - dimensions[2] + 1;
		return new int[] {endX, endY, endZ};
	}

	private static int[] getFacingEastCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endX = 0, endZ = 0, endY = 0;
		endZ = startZ - dimensions[2] + 1;
		endY = startY + dimensions[1] - 1;
		endX = startX + dimensions[0] - 1;
		return new int[] {endX, endY, endZ};
	}

	private static int[] getFacingSouthCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endX = 0, endZ = 0, endY = 0;
		endZ = startZ + dimensions[0] - 1;
		endY = startY + dimensions[1] - 1;
		endX = startX + dimensions[2] - 1;
		return new int[] {endX, endY, endZ};
	}

	private static int[] getFacingWestCoordinates(int startX, int startY, int startZ, int[] dimensions) {
		// TODO Auto-generated method stub
		int endX = 0, endZ = 0, endY = 0;
		endZ = startZ + dimensions[2] - 1;
		endY = startY + dimensions[1] - 1;
		endX = startX - dimensions[0] + 1;
		return new int[] {endX, endY, endZ};
	}

	private static String getGeneralDirection(int angle) {
		String specificDirection = getSpecificDirection(angle);
		Set<String> northDir = new HashSet<String>(Arrays.asList("north", "north northeast", "north northwest", "northeast"));
		Set<String> eastDir = new HashSet<String>(Arrays.asList("east", "east northeast", "east southeast", "southeast"));
		// Set<String> westDir = new HashSet<String>(Arrays.asList("north", "north northeast", "north northwest", "northeast"));
		Set<String> southDir = new HashSet<String>(Arrays.asList("south", "south southeast", "south southwest", "southwest"));
		
		if(northDir.contains(specificDirection)){
			return "north";
		}
		if(eastDir.contains(specificDirection)){
			return "east";
		}
		if(southDir.contains(specificDirection)){
			return "south";
		}
		return "west";
	}
	
	public static String getSpecificDirection(int angle) {
		if(angle < 0) {
			angle += 360;
		}
		int dirInt = (int) ((angle + 8) / 22.5);
		if(dirInt == 0)
			return "south";
		else if(dirInt == 1)
			return "south southwest";
		else if (dirInt == 2)
			return "southwest";
		else if(dirInt == 3)
			return "north southwest";
		else if(dirInt == 4)
			return "west"; // west
		else if(dirInt == 5)
			return "west northwest";
		else if(dirInt == 6)
			return "northwest";
		else if(dirInt == 7)
			return "north northwest";
		else if(dirInt == 8)
			return "north"; //north 
		else if(dirInt == 9)
			return "north northeast";
		else if(dirInt == 10)
			return "northeast";
		else if(dirInt == 11)
			return "south northeast";
		else if(dirInt == 12)
			return "east"; //east
		else if(dirInt == 13)
			return "east southeast";
		else if(dirInt == 14)
			return "southeast";
		else if(dirInt == 15)
			return "south southeast";
		else
			return "south"; // south
	}

}
