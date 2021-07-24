package com.multicraft.data;

import com.multicraft.exceptions.NoCommandHistoryException;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PreviousBuildRecords {

	private final HashMap<UUID, CustomUndoStack> buildUndoData = new HashMap<>();
	private final HashMap<UUID, CustomRedoStack> buildRedoData = new HashMap<>();
	private final int DEFAULT_UNDO_STACK_SIZE = 5;
	private final int DEFAULT_REDO_STACK_SIZE = 5;
	private static PreviousBuildRecords instance = null;
	
	public static PreviousBuildRecords getInstance() {
		if(instance == null) {
			instance = new PreviousBuildRecords();
		}
		return instance;
	}
	
	private PreviousBuildRecords() { }

	public void appendBuildRecord(Player p, BuildCommandRecord buildData) {
		if(! buildUndoData.containsKey(p.getUniqueId())) {
			buildUndoData.put(p.getUniqueId(), new CustomUndoStack(DEFAULT_UNDO_STACK_SIZE));
		}
		CustomUndoStack temp = buildUndoData.get(p.getUniqueId());
		temp.push(buildData);
		buildUndoData.put(p.getUniqueId(), temp);
	}
	
	public void clearPlayerRedo(Player p) {
		if(!buildRedoData.containsKey(p.getUniqueId())) {
			return;
		}
		CustomRedoStack temp = new CustomRedoStack(buildRedoData.get(p.getUniqueId()).getSize());
		buildRedoData.put(p.getUniqueId(), temp);
	}
	
	public BuildCommandRecord getPlayersBuildRecordForUndo(Player p) throws NoCommandHistoryException {
		
		BuildCommandRecord temp = null;
		if(! buildUndoData.containsKey(p.getUniqueId())) {
			throw new NoCommandHistoryException("Player is not in the dictionary.");
		}

		CustomUndoStack tempStack = buildUndoData.get(p.getUniqueId());
		temp = tempStack.pop();
		// temp = instance.buildsUndoData.get(p).pop();
		return temp;
	}

	public BuildCommandRecord getPlayersLastBuildRecord(Player p) throws NoCommandHistoryException{
		if(! buildUndoData.containsKey(p.getUniqueId())) {
			throw new NoCommandHistoryException("Player is not in the dictionary.");
		}
		CustomUndoStack tempStack = buildUndoData.get(p.getUniqueId());
		return tempStack.peek();
	}
	
	public BuildCommandRecord getPlayersBuildRecordForRedo(Player p) throws NoCommandHistoryException{
		if(! buildRedoData.containsKey(p.getUniqueId()))
			throw new NoCommandHistoryException("Player is not in the redo dictionary.");
		return instance.buildRedoData.get(p.getUniqueId()).pop();
	}
	
	public void addToRedoStack(Player p, BuildCommandRecord data) {
		if(! buildRedoData.containsKey(p.getUniqueId())) {
			buildRedoData.put(p.getUniqueId(), new CustomRedoStack(DEFAULT_REDO_STACK_SIZE));
		}
		CustomRedoStack temp = buildRedoData.get(p.getUniqueId());
		temp.push(data);
		buildRedoData.put(p.getUniqueId(), temp);
	}
	
	public void addToUndoStack(Player p, BuildCommandRecord data) {
		if(! buildUndoData.containsKey(p.getUniqueId())) {
			buildUndoData.put(p.getUniqueId(), new CustomUndoStack(DEFAULT_UNDO_STACK_SIZE));
		}
		CustomUndoStack temp = buildUndoData.get(p.getUniqueId());
		temp.push(data);
		buildUndoData.put(p.getUniqueId(), temp);
	}
	
	public int getPlayerUndoStackSize(Player p) {
		return instance.buildUndoData.get(p.getUniqueId()).getSize();
	}
	
	public BuildCommandRecord getItemAtIndexForPlayer(Player p, int i) {
		return buildUndoData.get(p.getUniqueId()).getItemAtIndex(i);
	}

}
