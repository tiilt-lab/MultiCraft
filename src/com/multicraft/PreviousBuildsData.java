package com.multicraft;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PreviousBuildsData {	
	private HashMap<UUID, CustomUndoStack> buildsUndoData = new HashMap<>();
	private HashMap<UUID, CustomRedoStack> buildsRedoData = new HashMap<>();
	private final int DEFAULT_UNDO_STACK_SIZE = 5;
	private final int DEFAULT_REDO_STACK_SIZE = 5;
	private static PreviousBuildsData instance = null;
	
	public static PreviousBuildsData getInstance() {
		if(instance == null) {
			instance = new PreviousBuildsData();
		}
		return instance;
	}
	
	private PreviousBuildsData() {
	}
		
	
	public void appendBuildRecord(Player p, BuildCommandData buildData) {
		if(! buildsUndoData.containsKey(p.getUniqueId())) {
			buildsUndoData.put(p.getUniqueId(), new CustomUndoStack(DEFAULT_UNDO_STACK_SIZE));
		}
		CustomUndoStack temp = buildsUndoData.get(p.getUniqueId());
		temp.push(buildData);
		buildsUndoData.put(p.getUniqueId(), temp);
	}
	
	public void clearPlayerRedo(Player p) {
		if(!buildsRedoData.containsKey(p.getUniqueId())) {
			return;
		}
		CustomRedoStack temp = new CustomRedoStack(buildsRedoData.get(p.getUniqueId()).getSize());
		buildsRedoData.put(p.getUniqueId(), temp);
	}
	
	public BuildCommandData getPlayersBuildRecordForUndo(Player p) throws NoCommandHistoryException{
		
		BuildCommandData temp = null;
		if(! buildsUndoData.containsKey(p.getUniqueId())) {
			throw new NoCommandHistoryException("Player is not in the dictionary.");
		}

		CustomUndoStack tempStack = buildsUndoData.get(p.getUniqueId());
		temp = tempStack.pop();
		// temp = instance.buildsUndoData.get(p).pop();
		return temp;
	}
	
	public BuildCommandData getPlayersBuildRecordForRedo(Player p) throws NoCommandHistoryException{
		if(! buildsRedoData.containsKey(p.getUniqueId()))
			throw new NoCommandHistoryException("Player is not in the redo dictionary.");
		BuildCommandData temp = null;
		temp = instance.buildsRedoData.get(p.getUniqueId()).pop();
		return temp;
	}
	
	public void addToRedoStack(Player p, BuildCommandData data) {
		if(! buildsRedoData.containsKey(p.getUniqueId())) {
			buildsRedoData.put(p.getUniqueId(), new CustomRedoStack(DEFAULT_REDO_STACK_SIZE));
		}
		CustomRedoStack temp = buildsRedoData.get(p.getUniqueId());
		temp.push(data);
		buildsRedoData.put(p.getUniqueId(), temp);
	}
	
	public void addToUndoStack(Player p, BuildCommandData data) {
		if(! buildsUndoData.containsKey(p.getUniqueId())) {
			buildsUndoData.put(p.getUniqueId(), new CustomUndoStack(DEFAULT_UNDO_STACK_SIZE));
		}
		CustomUndoStack temp = buildsUndoData.get(p.getUniqueId());
		temp.push(data);
		buildsUndoData.put(p.getUniqueId(), temp);
	}
	
	public int getPlayerUndoStackSize(Player p) {
		return instance.buildsUndoData.get(p.getUniqueId()).getSize();
	}
	
	public BuildCommandData getItemAtIndexForPlayer(Player p, int i) {
		return buildsUndoData.get(p.getUniqueId()).getItemAtIndex(i);
	}
}
