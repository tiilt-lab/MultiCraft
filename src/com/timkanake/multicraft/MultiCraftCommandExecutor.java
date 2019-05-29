package com.timkanake.multicraft;


import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.timkanake.multicraft.Materials.MaterialDoesNotExistException;

public class MultiCraftCommandExecutor implements CommandExecutor{
	private final MultiCraft plugin;
	
	public MultiCraftCommandExecutor(MultiCraft plugin) {
		this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("mbuild")) {
			// TODO: Give user feedback
			if(args.length < 3)
				return false;
			
			// parse args to dimensions
			int[] dimensions = new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
			
			// Location playerLoc = p.getLocation();
			List<Block> blocks = p.getLineOfSight((Set<Material>) null, 5);
			Location startLocation = blocks.get(4).getLocation();
			
			int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(startLocation, dimensions);
			Location endLoc = new Location(startLocation.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
			
			
			// TODO: Get the material
			
			int materialId = 1;
			
			if(args.length > 3) {
				// TODO: Check if int or string
				try {
					materialId = Integer.parseInt(args[3]);
				}catch(NumberFormatException e) {
					try {
						materialId = Materials.getId(args[3]);
					}catch(MaterialDoesNotExistException f) {
						// TODO: Give signal
						materialId = 1;
					}
				}
			}
			
			
			Material material = Material.getMaterial(materialId);
			
			GameCommand gComm = new GameCommand(this.plugin);
			
			if(args.length > 4)
				buildHollow(dimensions, startLocation,  endLoc, gComm, material);
			else
				gComm.updateBlocks(startLocation, endLoc, material);
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
