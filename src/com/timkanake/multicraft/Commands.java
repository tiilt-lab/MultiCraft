package com.timkanake.multicraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Commands {	
	
	public static boolean undo(Player p, MultiCraft plugin) {
		PreviousBuildsData pData = PreviousBuildsData.getInstance();
		BuildCommandData playerBuildRecord = new BuildCommandData();
		
		// get the player's build record
		try {
			playerBuildRecord = pData.getPlayersBuildRecordForUndo(p);
		}catch(NoCommandHistoryException e) {
			p.sendMessage("You have no build record available.");
			return false;
		}
		
		// restore blocks			
		List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
		List<BlockRecord> blocksAffectedDuringUndo = new ArrayList<BlockRecord>();
		World world = p.getWorld();
		for (BlockRecord b: blocksToChange) {		
			Block t = world.getBlockAt(b.x, b.y, b.z);
			blocksAffectedDuringUndo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
			try {			
				t.setType(b.material);
			}catch(Exception e) {
				p.sendMessage("Failed to update blocks.");
				return false;
			}				
		}			
		
		// update re-do stack
		BuildCommandData toStoreInRedo = new BuildCommandData(blocksAffectedDuringUndo, blocksAffectedDuringUndo.size());
		pData.addToRedoStack(p, toStoreInRedo);
		return true;
	}
	
	public static boolean redo(Player p, MultiCraft plugin) {
		// get data from redoStack
		PreviousBuildsData pData = PreviousBuildsData.getInstance();
		BuildCommandData playerBuildRecord;
		
		try {
			playerBuildRecord = pData.getPlayersBuildRecordForRedo(p);
		}catch (NoCommandHistoryException e) {
			// TODO: Customize message to be displayed to the player only
			p.sendMessage("You have no build record available for redo.");
			return false;
		}
		
		
		List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
		List<BlockRecord> blocksAffectedDuringRedo = new ArrayList<BlockRecord>();
		World world = p.getWorld();
		for (BlockRecord b: blocksToChange) {		
			Block t = world.getBlockAt(b.x, b.y, b.z);
			blocksAffectedDuringRedo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
			try {			
				t.setType(b.material);
			}catch(Exception e) {
				p.sendMessage("Failed to update blocks.");
				return false;
			}				
		}		
		
		// restore blocks
		BuildCommandData toStoreInUndo = new BuildCommandData(blocksAffectedDuringRedo, blocksAffectedDuringRedo.size());
		pData.addToUndoStack(p, toStoreInUndo);
		return true;
	}
	
	public static List<BlockRecord> buildStructure(Location startLoc, int[] dimensions, Material m, boolean isHollow){
		int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(startLoc, dimensions);
		Location endLoc = new Location(startLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
		List<BlockRecord> blocksAffected = new ArrayList<BlockRecord>();
		
		
		if(isHollow) {
			blocksAffected = buildHollow(dimensions, startLoc, endLoc, m);
			return blocksAffected;
		}else {
			blocksAffected = updateBlocks(startLoc, endLoc, m);
		}
		
		return blocksAffected;
	}
	
	public static List<BlockRecord> buildHollow(int[] dimensions, Location startLoc, Location endLoc, Material m) {	
		List<BlockRecord> blocksAffected = new ArrayList<BlockRecord>();
		// bottom Wall
		blocksAffected.addAll(GameCommand.updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m));
		
		// top wall
		blocksAffected.addAll(GameCommand.updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), 
				endLoc, m));
		
		// back wall
		blocksAffected.addAll(GameCommand.updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), startLoc.getY(), endLoc.getZ()), endLoc, m));
		
		// front wall
		blocksAffected.addAll(GameCommand.updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), endLoc.getY(), startLoc.getZ()), m));

		// right wall
		blocksAffected.addAll(GameCommand.updateBlocks(new Location(startLoc.getWorld(), endLoc.getX(), startLoc.getY(), startLoc.getZ()), endLoc, m));
		
		// left wall
		blocksAffected.addAll(GameCommand.updateBlocks(startLoc, new Location(endLoc.getWorld(), startLoc.getX(), endLoc.getY(), endLoc.getZ()), m));
		
		return blocksAffected;
	}
	
	public static List<BlockRecord>  updateBlocks(Location pos1, Location pos2, Material m) {
		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = pos1.getBlockX() < pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		maxX = pos1.getBlockX() >= pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		minY = pos1.getBlockY() < pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		maxY = pos1.getBlockY() >= pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		minZ = pos1.getBlockZ() < pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		maxZ = pos1.getBlockZ() >= pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		
		List<BlockRecord> blocksAffected = new ArrayList<BlockRecord>();

		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				for (int y = minY; y <= maxY; ++y) {
					blocksAffected.add(updateBlock(world, x, y, z, m,(byte) 0));
				}
			}
		}
		return blocksAffected;
	}
	
	private static BlockRecord updateBlock(World world, int x, int y, int z, Material blockType, byte blockData) {
		Block thisBlock = world.getBlockAt(x,y,z);
		return updateBlock(thisBlock, blockType, blockData);
		
	}

	private static BlockRecord updateBlock(Block block, Material m, byte blockData) {
		BlockRecord toReturn = new BlockRecord(block.getType(), block.getX(), block.getY(), block.getZ());
		try {			
			block.setType(m);
		}catch(Exception e) {
			// TODO: Handle this mess
		}
		return toReturn;
	}

	public static void updateUndoAndRedoStacks(List<BlockRecord> blocksAffected, Player p) {
		BuildCommandData affectedBlocksData = new BuildCommandData(blocksAffected, blocksAffected.size());
		PreviousBuildsData pData = PreviousBuildsData.getInstance();
		pData.clearPlayerRedo(p);
		pData.appendBuildRecord(p, affectedBlocksData);		
	}

}
