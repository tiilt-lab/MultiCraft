package com.timkanake.multicraft;

import java.util.ArrayList;
import java.util.List;

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
			// TODO: Customize message to be displayed to the player only
			plugin.getServer().broadcastMessage("You have no build record");
			plugin.getServer().broadcastMessage(e.getMessage());
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
				// TODO: Handle this
				plugin.getServer().broadcastMessage(e.toString());
				plugin.getServer().broadcastMessage("Failed to update Blocks :(");
				return false;
			}				
		}			
		
		// update redo stack
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
			plugin.getServer().broadcastMessage("You have no build record for redo");
			plugin.getServer().broadcastMessage(e.getMessage());
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
				// TODO: Handle This
				plugin.getServer().broadcastMessage(e.toString());
				plugin.getServer().broadcastMessage("Failed to update Blocks :(");
			}				
		}		
		
		// restore blocks
		BuildCommandData toStoreInUndo = new BuildCommandData(blocksAffectedDuringRedo, blocksAffectedDuringRedo.size());
		pData.addToUndoStack(p, toStoreInUndo);
		return true;
	}

}
