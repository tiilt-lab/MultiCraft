package com.timkanake.multicraft;

import java.util.HashMap;

import org.bukkit.entity.Player;


public class PreviousBuildsData {	
	private HashMap<Player, CustomUndoStack> buildsUndoData = new HashMap<Player, CustomUndoStack>();
	private HashMap<Player, CustomRedoStack> buildsRedoData = new HashMap<Player, CustomRedoStack>();
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
		if(! buildsUndoData.containsKey(p)) {
			buildsUndoData.put(p, new CustomUndoStack(DEFAULT_UNDO_STACK_SIZE));
		}
		CustomUndoStack temp = buildsUndoData.get(p);
		temp.push(buildData);
		buildsUndoData.put(p, temp);
	}
	
	public void clearPlayerRedo(Player p) {
		if(!buildsRedoData.containsKey(p)) {
			return;
		}
		CustomRedoStack temp = new CustomRedoStack(buildsRedoData.get(p).getSize());
		buildsRedoData.put(p, temp);
	}
	
	public BuildCommandData getPlayersBuildRecordForUndo(Player p) throws NoCommandHistoryException{
		
		BuildCommandData temp = null;
		if(! buildsUndoData.containsKey(p))
			throw new NoCommandHistoryException();
		
		try {
			CustomUndoStack tempStack = instance.buildsUndoData.get(p);
			temp = tempStack.pop();
//			temp = instance.buildsUndoData.get(p).pop();
		}catch(NoCommandHistoryException e) {
			throw e;
		}
		return temp;
	}
	
	public BuildCommandData getPlayersBuildRecordForRedo(Player p) throws NoCommandHistoryException{
		if(! buildsRedoData.containsKey(p))
			throw new NoCommandHistoryException();
		BuildCommandData temp = null;
		try {
			temp = instance.buildsRedoData.get(p).pop();
		}catch(NoCommandHistoryException e) {
			throw e;
		}
		return temp;
	}
	
	public void addToRedoStack(Player p, BuildCommandData data) {
		if(! buildsRedoData.containsKey(p)) {
			buildsRedoData.put(p, new CustomRedoStack(DEFAULT_REDO_STACK_SIZE));
		}
		CustomRedoStack temp = buildsRedoData.get(p);
		temp.push(data);
		buildsRedoData.put(p, temp);
	}
	
	public int getPlayerUndoStackSize(Player p) {
		return instance.buildsUndoData.get(p).getSize();
	}
}
