package com.timkanake.multicraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;

import org.bukkit.Location;

public class MultiCraftBuildsListener {
	private static Clock cl = Clock.systemDefaultZone();
	
	public static void recordBuild(String displayName, Location playerLoc, int[] dimensions, int materialId,
			int hollowFlag) throws SQLException {
		Connection  c = MySQL.getConnection();
		int timeInMilliseconds = (int) cl.millis();
		PreparedStatement prepStatement = c.prepareStatement("insert into MultiCraft.multicraft_block_placement values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		prepStatement.setString(1, displayName);
		prepStatement.setInt(2,  timeInMilliseconds);
		
		prepStatement.setDouble(3, playerLoc.getX());
		prepStatement.setDouble(4, playerLoc.getY());
		prepStatement.setDouble(5, playerLoc.getZ());
		
		prepStatement.setInt(6, dimensions[0]);
		prepStatement.setInt(7, dimensions[1]);
		prepStatement.setInt(8, dimensions[2]);
		
		prepStatement.setInt(9, materialId);
		prepStatement.setInt(10, hollowFlag);
		
		prepStatement.executeUpdate();
	}
}
