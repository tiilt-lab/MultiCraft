package com.multicraft;

import com.multicraft.data.BlockRecord;
import com.multicraft.data.BuildCommandRecord;
import com.multicraft.data.PreviousBuildRecords;
import com.multicraft.exceptions.MaterialDoesNotExistException;
import com.multicraft.exceptions.NoCommandHistoryException;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Commands {

	public static boolean undo(Player p, MultiCraft plugin) {
		PreviousBuildRecords pData = PreviousBuildRecords.getInstance();
		BuildCommandRecord playerBuildRecord;
		
		// get the player's build record
		try {
			playerBuildRecord = pData.getPlayersBuildRecordForUndo(p);
		} catch (NoCommandHistoryException e) {
			p.sendMessage("You have no build record available.");
			return false;
		}
		
		// restore blocks
		List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
		List<BlockRecord> blocksAffectedDuringUndo = new ArrayList<>();
		World world = p.getWorld();
		for (BlockRecord b : blocksToChange) {
			Block t = world.getBlockAt(b.x, b.y, b.z);
			blocksAffectedDuringUndo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
			Bukkit.getScheduler().runTask(plugin, () -> t.setType(b.material));
		}

		if (p.getGameMode() == GameMode.SURVIVAL) {
			Material m = blocksAffectedDuringUndo.get(0).material;
			HashSet<BlockRecord> uniqueBlocks = new HashSet<>(blocksAffectedDuringUndo);
			int n = uniqueBlocks.size();
			p.getInventory().addItem(new ItemStack(m, n));
			p.sendMessage("Returned " + n + " " + m + " to your inventory.");
		}
		
		// update re-do stack
		BuildCommandRecord toStoreInRedo = new BuildCommandRecord(blocksAffectedDuringUndo, blocksAffectedDuringUndo.size());
		pData.addToRedoStack(p, toStoreInRedo);
		return true;
	}
	
	public static boolean redo(Player p, MultiCraft plugin) {
		// get data from redoStack
		PreviousBuildRecords pData = PreviousBuildRecords.getInstance();
		BuildCommandRecord playerBuildRecord;
		
		try {
			playerBuildRecord = pData.getPlayersBuildRecordForRedo(p);
		} catch (NoCommandHistoryException e) {
			p.sendMessage("You have no build record available for redo.");
			return false;
		}

		List<BlockRecord> blocksToChange = playerBuildRecord.blocksAffected;
		List<BlockRecord> blocksAffectedDuringRedo = new ArrayList<>();

		if (p.getGameMode() == GameMode.SURVIVAL) {
			Material m = blocksToChange.get(0).material;
			HashSet<BlockRecord> uniqueBlocks = new HashSet<>(blocksToChange);
			int n = uniqueBlocks.size();
			if (!p.getInventory().contains(m, n)) {
				p.sendMessage("You do not have the required block(s).");
				return false;
			}
			p.getInventory().removeItem(new ItemStack(m, n));
			p.sendMessage("Removed " + n + " " + m + " from your inventory.");
		}

		World world = p.getWorld();
		for (BlockRecord b : blocksToChange) {
			Block t = world.getBlockAt(b.x, b.y, b.z);
			blocksAffectedDuringRedo.add(new BlockRecord(t.getType(), t.getX(), t.getY(), t.getZ()));
			Bukkit.getScheduler().runTask(plugin, () -> t.setType(b.material));
		}

		
		// restore blocks
		BuildCommandRecord toStoreInUndo = new BuildCommandRecord(blocksAffectedDuringRedo, blocksAffectedDuringRedo.size());
		pData.addToUndoStack(p, toStoreInUndo);
		return true;
	}

	public static boolean build(Player p, Location playerLoc, Location startLoc, int[] dimensions, String materialArg,
								boolean isHollow, boolean inSurvival, MultiCraft plugin) {
		Material material;
		try {
			material = Materials.getMaterial(materialArg);
		} catch (MaterialDoesNotExistException e) {
			p.sendMessage(e.getMessage());
			return false;
		}

		if (inSurvival) {
			int numBlocksRequired = dimensions[0] * dimensions[1] * dimensions[2];
			if (isHollow) {
				numBlocksRequired -= (dimensions[0] - 2) * (dimensions[1] - 2) * (dimensions[2] - 2);
			}
			if (!p.getInventory().contains(material, numBlocksRequired)) {
				p.sendMessage("You do not have the required block(s).");
				return false;
			} else {
				p.getInventory().removeItem(new ItemStack(material, numBlocksRequired));
				p.sendMessage("Used " + numBlocksRequired + " blocks.");
			}
		}

		List<BlockRecord> blocksAffected = buildStructure(playerLoc, startLoc, dimensions, material, isHollow, plugin);
		updateUndoAndRedoStacks(blocksAffected, p);
		return true;
	}
	
	public static List<BlockRecord> buildStructure(Location playerLoc, Location startLoc, int[] dimensions,
												   Material m, boolean isHollow, MultiCraft plugin) {
		int[] buildCoordinates = CoordinatesCalculator.getBuildCoordinates(playerLoc, startLoc, dimensions);
		Location endLoc = new Location(startLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);
		List<BlockRecord> blocksAffected;

		blocksAffected = isHollow ? buildHollow(startLoc, endLoc, m, plugin) : updateBlocks(startLoc, endLoc, m, plugin);
		
		return blocksAffected;
	}
	
	public static List<BlockRecord> buildHollow(Location startLoc, Location endLoc, Material m, MultiCraft plugin) {
		List<BlockRecord> blocksAffected = new ArrayList<>();
		// bottom Wall
		blocksAffected.addAll(updateBlocks(startLoc,
				new Location(endLoc.getWorld(), endLoc.getX(), startLoc.getY(), endLoc.getZ()), m, plugin));

		// front wall
		blocksAffected.addAll(updateBlocks(startLoc,
				new Location(endLoc.getWorld(), endLoc.getX(), endLoc.getY(), startLoc.getZ()), m, plugin));

		// left wall
		blocksAffected.addAll(updateBlocks(startLoc,
				new Location(endLoc.getWorld(), startLoc.getX(), endLoc.getY(), endLoc.getZ()), m, plugin));

		// back wall
		blocksAffected.addAll(updateBlocks(
				new Location(startLoc.getWorld(), startLoc.getX(), startLoc.getY(), endLoc.getZ()), endLoc, m, plugin));

		// right wall
		blocksAffected.addAll(updateBlocks(
				new Location(startLoc.getWorld(), endLoc.getX(), startLoc.getY(), startLoc.getZ()), endLoc, m, plugin));

		// top wall
		blocksAffected.addAll(updateBlocks(
				new Location(startLoc.getWorld(), startLoc.getX(), endLoc.getY(), startLoc.getZ()), endLoc, m, plugin));
		
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

	public static List<BlockRecord> buildTStructure(Location playerLoc, Location startLoc, JSONObject blockMap, MultiCraft plugin) {
		int[] dimensions = {10, 10, 10};
		int[] buildCoordinates = CoordinatesCalculator.getBuildCoordinates(playerLoc, startLoc, dimensions);
		Location endLoc = new Location(startLoc.getWorld(), buildCoordinates[0], buildCoordinates[1], buildCoordinates[2]);

		return updateTBlocks(startLoc, endLoc, Material.STONE, blockMap, plugin);
	}

	@SuppressWarnings("deprecation")
	public static List<BlockRecord> updateTBlocks(Location pos1, Location pos2, Material m, JSONObject blockMap, MultiCraft plugin) {
		int startX = min(pos1.getBlockX(), pos2.getBlockX());
		int startY = min(pos1.getBlockY(), pos2.getBlockY());
		int startZ = min(pos1.getBlockZ(), pos2.getBlockZ());
		int endX = max(pos1.getBlockX(), pos2.getBlockX());
		int endY = max(pos1.getBlockY(), pos2.getBlockY());
		int endZ = max(pos1.getBlockZ(), pos2.getBlockZ());

		World world = pos1.getWorld();
		List<BlockRecord> blocksAffected = new ArrayList<>();

		for (int x = startX; x <= endX; x++) {
			String relativeX = Integer.toString(x - startX);
			JSONObject blockMapY = null;
			if (blockMap != null) {
				if (!blockMap.containsKey(relativeX)) continue;
				else blockMapY = (JSONObject) blockMap.get(relativeX);
			}
			for (int y = startY; y <= endY; y++) {
				String relativeY = Integer.toString(y - startY);
				JSONObject blockMapZ = null;
				if (blockMapY != null) {
					if (!blockMapY.containsKey(relativeY)) continue;
					else blockMapZ = ((JSONObject) blockMapY.get(relativeY));
				}
				for (int z = startZ; z <= endZ; z++) {
					String relativeZ = Integer.toString(z - startZ);
					if (blockMapZ != null) {
						if (!blockMapZ.containsKey(relativeZ)) continue;
						else {
							int id = ((Long) blockMapZ.get(relativeZ)).intValue();
							m = Material.getMaterial(id);
						}
					}
					blocksAffected.add(updateBlock(world, plugin, x, y, z, m));
				}
			}
		}

		return blocksAffected;
	}

	
	private static BlockRecord updateBlock(World world, MultiCraft plugin, int x, int y, int z, Material blockType) {
		Block thisBlock = world.getBlockAt(x, y, z);
		BlockRecord toReturn = new BlockRecord(thisBlock.getType(), x, y, z);
		Bukkit.getScheduler().runTask(plugin, () -> thisBlock.setType(blockType));

		return toReturn;
	}

	public static void updateUndoAndRedoStacks(List<BlockRecord> blocksAffected, Player p) {
		BuildCommandRecord affectedBlocksData = new BuildCommandRecord(blocksAffected, blocksAffected.size());
		PreviousBuildRecords pData = PreviousBuildRecords.getInstance();
		pData.clearPlayerRedo(p);
		pData.appendBuildRecord(p, affectedBlocksData);		
	}

}
