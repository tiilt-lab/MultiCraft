package com.multicraft.data;

import com.multicraft.exceptions.NoCommandHistoryException;

public class CustomRedoStack {

	public CustomUndoStack redoStack;
	
	public CustomRedoStack(int size) {
		this.redoStack = new CustomUndoStack(size);
	}
	
	public BuildCommandRecord pop() throws NoCommandHistoryException {
		return this.redoStack.pop();
	}
	
	public void push(BuildCommandRecord data) {
		this.redoStack.push(data);
	}
	
	public boolean isEmpty() {
		return this.redoStack.isEmpty();
	}
	
	public int getSize() {
		return this.redoStack.size;
	}

}
