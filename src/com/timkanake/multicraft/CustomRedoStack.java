package com.timkanake.multicraft;

import com.timkanake.multicraft.CustomUndoStack.NoCommandHistoryException;

public class CustomRedoStack {
	public CustomUndoStack redoStack;
	
	public CustomRedoStack(int size) {
		this.redoStack = new CustomUndoStack(size);
	}
	
	public BuildCommandData pop() throws NoCommandHistoryException {
		try {
			return this.redoStack.pop();
		}catch(NoCommandHistoryException e) {
			throw e;
		}		 
	}
	
	public void push(BuildCommandData data) {
		this.redoStack.push(data);
	}
	
	public boolean isEmpty() {
		return this.redoStack.isEmpty();
	}
	
	public int getSize() {
		return this.redoStack.size;
	}
}
