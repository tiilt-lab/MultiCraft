package com.timkanake.multicraft;


import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiCraftCommandExecutor implements CommandExecutor{
	private final MultiCraft plugin;
	private static int HOLLOW_FLAG = 0;
	
	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("mbuild")) {
			if(args.length < 3)
				return false;
			
			// parse args to dimensions
			int[] dimensions = new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
			
			Location playerLoc = p.getLocation();
			int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(playerLoc, dimensions);
			Location endLoc = new Location(playerLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
			
			Material material = Material.getMaterial(1);
			int materialId = 1;
			if(args.length > 3) {
				material = Material.getMaterial(Integer.parseInt(args[3]));
				materialId = Integer.parseInt(args[3]);
			}
			
			
			GameCommand gComm = new GameCommand(this.plugin);
			
			if(args.length > 4)
				buildHollow(dimensions, playerLoc,  endLoc, gComm, material);
			else
				gComm.updateBlocks(playerLoc, endLoc, material);
//			
//			try {
//				MultiCraftBuildsListener.recordBuild(p.getDisplayName(), playerLoc, dimensions, materialId, HOLLOW_FLAG);
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			} catch(Exception e) {
//				//do nothing;
//			}
			
			return true;
		}
		return false;
	}
	
	public void buildHollow(int[] dimensions, Location startLoc, Location endLoc, GameCommand gComm, Material m) {		
		// bottom Wall
		gComm.updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m);
		
		// top wall
		gComm.updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), 
				endLoc, m);
		
		// back wall
		gComm.updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), startLoc.getY(), endLoc.getZ()), endLoc, m);
		
		// front wall
		gComm.updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), endLoc.getY(), startLoc.getZ()), m);

		// right wall
		gComm.updateBlocks(new Location(startLoc.getWorld(), endLoc.getX(), startLoc.getY(), startLoc.getZ()), endLoc, m);
		
		// left wall
		gComm.updateBlocks(startLoc, new Location(endLoc.getWorld(), startLoc.getX(), endLoc.getY(), endLoc.getZ()), m);
	}
	

}
