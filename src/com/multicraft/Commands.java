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
	
	public static List<BlockRecord> buildStructure(Location playerLoc, Location startLoc, int[] dimensions, Material m, boolean isHollow, MultiCraft plugin){
		int[] buildCoordinates = CoordinateCalculations.getBuildCoordinates(playerLoc, startLoc, dimensions);
		Location endLoc = new Location(startLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
		List<BlockRecord> blocksAffected;

		blocksAffected = isHollow ? buildHollow(startLoc, endLoc, m, plugin) : updateBlocks(startLoc, endLoc, m, plugin);
		
		return blocksAffected;
	}
	
	public static List<BlockRecord> buildHollow(Location startLoc, Location endLoc, Material m, MultiCraft plugin) {
		List<BlockRecord> blocksAffected = new ArrayList<>();
		// bottom Wall
		blocksAffected.addAll(updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m, plugin));

		// front wall
		blocksAffected.addAll(updateBlocks(startLoc, new Location(endLoc.getWorld(), endLoc.getX(), endLoc.getY(), startLoc.getZ()), m, plugin));

		// left wall
		blocksAffected.addAll(updateBlocks(startLoc, new Location(endLoc.getWorld(), startLoc.getX(), endLoc.getY(), endLoc.getZ()), m, plugin));

		// back wall
		blocksAffected.addAll(updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), startLoc.getY(), endLoc.getZ()), endLoc, m, plugin));

		// right wall
		blocksAffected.addAll(updateBlocks(new Location(startLoc.getWorld(), endLoc.getX(), startLoc.getY(), startLoc.getZ()), endLoc, m, plugin));

		// top wall
		blocksAffected.addAll(updateBlocks(new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), endLoc, m, plugin));
		
		return blocksAffected;
	}
	
	public static List<BlockRecord> updateBlocks(Location pos1, Location pos2, Material m, MultiCraft plugin) {
		boolean incrementX = pos1.getX() <= pos2.getX();
		boolean incrementY = pos1.getY() <= pos2.getY();
		boolean incrementZ = pos1.getZ() <= pos2.getZ();

		World world = pos1.getWorld();
		List<BlockRecord> blocksAffected = new ArrayList<>();

		int x = (int) pos1.getX();
		while ((incrementX && x <= pos2.getX()) || (!incrementX && x >= pos2.getX())) {
			int y = (int) pos1.getY();
			while ((incrementY && y <= pos2.getY()) || (!incrementY && y >= pos2.getY())) {
				int z = (int) pos1.getZ();
				while ((incrementZ && z <= pos2.getZ()) || (!incrementZ && z >= pos2.getZ())) {
					blocksAffected.add(updateBlock(world, plugin, x, y, z, m));
					if (incrementZ) ++z;
					else --z;
				}
				if (incrementY) ++y;
				else --y;
			}
			if (incrementX) ++x;
			else --x;
		}

		return blocksAffected;
	}
	
	private static BlockRecord updateBlock(World world, MultiCraft plugin, int x, int y, int z, Material blockType) {
		Block thisBlock = world.getBlockAt(x, y, z);
		BlockRecord toReturn = new BlockRecord(thisBlock.getType(), x, y, z);
		Bukkit.getScheduler().runTask(plugin, () -> { thisBlock.setType(blockType); });

		return toReturn;
	}

	public static void updateUndoAndRedoStacks(List<BlockRecord> blocksAffected, Player p) {
		BuildCommandData affectedBlocksData = new BuildCommandData(blocksAffected, blocksAffected.size());
		PreviousBuildsData pData = PreviousBuildsData.getInstance();
		pData.clearPlayerRedo(p);
		pData.appendBuildRecord(p, affectedBlocksData);		
	}

}
