package com.timkanake.multicraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
			List<Block> blocks = p.getLineOfSight((Set<Material>) null, 6);
			
			
			Material tempMaterial = null;
			// get Start Location, defaults to the block in front of the player
			Location startLocation = blocks.get(0).getLocation();
			for(Block b : blocks) {
				tempMaterial = b.getType();
				if(! tempMaterial.equals(Material.AIR)) {
					startLocation = b.getLocation().add(0, 1, 0);
				}else {
					this.plugin.getServer().broadcastMessage("An airblock has been detected in the line of sight");
				}
			}
			
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
			List<Block> blocksAffected = new ArrayList<Block>();
			if(args.length > 4)
				blocksAffected = buildHollow(dimensions, startLocation,  endLoc, gComm, material);
			else
				blocksAffected = gComm.updateBlocks(startLocation, endLoc, material);
			
			BuildCommandData affectedBlocksData = new BuildCommandData(blocksAffected, blocksAffected.size());
			PreviousBuildsData.getInstance().clearPlayerRedo(p);
			PreviousBuildsData.getInstance().appendBuildRecord(p, affectedBlocksData);			
			return true;
		}else if(cmd.getName().equalsIgnoreCase("mundo")) {
			// do nothing for now
			// TODO: Implement this
			
			BuildCommandData playerBuildRecord;
			// get their build record
			try {
				playerBuildRecord = PreviousBuildsData.getInstance().getPlayersBuildRecordForUndo(p);
			}catch(NoCommandHistoryException e) {
				this.plugin.getServer().broadcastMessage("You have no build record");
				return false;
			}
			
			// restore blocks			
			List<Block> blocksToChange = playerBuildRecord.blocksAffected;
			List<Block> blocksAffectedDuringUndo = new ArrayList<Block>();
			World world = p.getWorld();
			for (Block b: blocksToChange) {				
				Block t = world.getBlockAt(b.getX(), b.getY(), b.getZ());
				t.setType(b.getType());
				blocksAffectedDuringUndo.add(t);
			}			
			
			// update redo stack
			BuildCommandData toStoreInRedo = new BuildCommandData(blocksAffectedDuringUndo, blocksAffectedDuringUndo.size());
			PreviousBuildsData.getInstance().addToRedoStack(p, toStoreInRedo);	
			
		}else if(cmd.getName().equalsIgnoreCase("mredo")) {
			// TODO: Implement this
			// get data from redoStack
			
			// restore block
			
			// update undo stack
		}
		return false;
	}
	
	public List<Block> buildHollow(int[] dimensions, Location startLoc, Location endLoc, GameCommand gComm, Material m) {	
		List<Block> blocksAffected = new ArrayList<Block>();
		// bottom Wall
		blocksAffected.addAll(gComm.updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m));
		
		// top wall
		blocksAffected.addAll(gComm.updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), 
				endLoc, m));
		
		// back wall
		blocksAffected.addAll(gComm.updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), startLoc.getY(), endLoc.getZ()), endLoc, m));
		
		// front wall
		blocksAffected.addAll(gComm.updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), endLoc.getY(), startLoc.getZ()), m));

		// right wall
		blocksAffected.addAll(gComm.updateBlocks(new Location(startLoc.getWorld(), endLoc.getX(), startLoc.getY(), startLoc.getZ()), endLoc, m));
		
		// left wall
		blocksAffected.addAll(gComm.updateBlocks(startLoc, new Location(endLoc.getWorld(), startLoc.getX(), endLoc.getY(), endLoc.getZ()), m));
		
		return blocksAffected;
	}
	

}
