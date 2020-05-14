package com.multicraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static java.lang.Math.*;

public class Commands {
	
	public static boolean undo(Player p, MultiCraft plugin) {
		PreviousBuildsData pData = PreviousBuildsData.getInstance();
		BuildCommandData playerBuildRecord;
		
		// get the player's build record
		try {
			playerBuildRecord = pData.getPlayersBuildRecordForUndo(p);
		}catch(NoCommandHistoryException e) {
			p.sendMessage("You have no build record available.");
			return false;
		}
		
		// restore blocks			
		List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
		List<BlockRecord> blocksAffectedDuringUndo = new ArrayList<>();
		World world = p.getWorld();
		for (BlockRecord b: blocksToChange) {		
			Block t = world.getBlockAt(b.x, b.y, b.z);
			blocksAffectedDuringUndo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
			Bukkit.getScheduler().runTask(plugin, () -> t.setType(b.material));
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
			p.sendMessage("You have no build record available for redo.");
			return false;
		}
		
		
		List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
		List<BlockRecord> blocksAffectedDuringRedo = new ArrayList<>();
		World world = p.getWorld();
		for (BlockRecord b: blocksToChange) {		
			Block t = world.getBlockAt(b.x, b.y, b.z);
			blocksAffectedDuringRedo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
			Bukkit.getScheduler().runTask(plugin, () -> t.setType(b.material));
		}		
		
		// restore blocks
		BuildCommandData toStoreInUndo = new BuildCommandData(blocksAffectedDuringRedo, blocksAffectedDuringRedo.size());
		pData.addToUndoStack(p, toStoreInUndo);
		return true;
	}
	
	public static List<BlockRecord> buildStructure(Location startLoc, int[] dimensions, Material m, boolean isHollow, MultiCraft plugin){
		int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(startLoc, dimensions);
		Location endLoc = new Location(startLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
		List<BlockRecord> blocksAffected;

		if(isHollow)
			blocksAffected = buildHollow(dimensions, startLoc, endLoc, m, plugin);
		else
			blocksAffected = updateBlocks(startLoc, endLoc, m, plugin);
		
		return blocksAffected;
	}
	
	public static List<BlockRecord> buildHollow(int[] dimensions, Location startLoc, Location endLoc, Material m, MultiCraft plugin) {
		List<BlockRecord> blocksAffected = new ArrayList<>();
		// bottom Wall
		blocksAffected.addAll(updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m, plugin));
		
		// top wall
		blocksAffected.addAll(updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), endLoc, m, plugin));
		
		// back wall
		blocksAffected.addAll(updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), startLoc.getY(), endLoc.getZ()), endLoc, m, plugin));
		
		// front wall
		blocksAffected.addAll(updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), endLoc.getY(), startLoc.getZ()), m, plugin));

		// right wall
		blocksAffected.addAll(updateBlocks(new Location(startLoc.getWorld(), endLoc.getX(), startLoc.getY(), startLoc.getZ()), endLoc, m, plugin));
		
		// left wall
		blocksAffected.addAll(updateBlocks(startLoc, new Location(endLoc.getWorld(), startLoc.getX(), endLoc.getY(), endLoc.getZ()), m, plugin));
		
		return blocksAffected;
	}
	
	public static List<BlockRecord>  updateBlocks(Location pos1, Location pos2, Material m, MultiCraft plugin) {
		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = min(pos1.getBlockX(), pos2.getBlockX());
		maxX = max(pos1.getBlockX(), pos2.getBlockX());
		minY = min(pos1.getBlockY(), pos2.getBlockY());
		maxY = max(pos1.getBlockY(), pos2.getBlockY());
		minZ = min(pos1.getBlockZ(), pos2.getBlockZ());
		maxZ = max(pos1.getBlockZ(), pos2.getBlockZ());
		
		List<BlockRecord> blocksAffected = new ArrayList<>();

		for (int x = minX; x <= maxX; ++x)
			for (int z = minZ; z <= maxZ; ++z)
				for (int y = minY; y <= maxY; ++y)
					blocksAffected.add(updateBlock(world, plugin, x, y, z, m,(byte) 0));

		return blocksAffected;
	}
	
	private static BlockRecord updateBlock(World world, MultiCraft plugin, int x, int y, int z, Material blockType, byte blockData) {
		Block thisBlock = world.getBlockAt(x,y,z);
		return updateBlock(thisBlock, blockType, plugin, blockData);
	}

	private static BlockRecord updateBlock(Block block, Material m, MultiCraft plugin, byte blockData) {
		BlockRecord toReturn = new BlockRecord(block.getType(), block.getX(), block.getY(), block.getZ());
		Bukkit.getScheduler().runTask(plugin, () -> { block.setType(m); });
		return toReturn;
	}

	public static void updateUndoAndRedoStacks(List<BlockRecord> blocksAffected, Player p) {
		BuildCommandData affectedBlocksData = new BuildCommandData(blocksAffected, blocksAffected.size());
		PreviousBuildsData pData = PreviousBuildsData.getInstance();
		pData.clearPlayerRedo(p);
		pData.appendBuildRecord(p, affectedBlocksData);		
	}

}
