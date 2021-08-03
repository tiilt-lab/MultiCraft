package com.multicraft.data;

import com.multicraft.exceptions.NoCommandHistoryException;

public class CustomUndoStack {

	BuildCommandRecord[] dataArr;
	int insertIndex;
	int size;

	public CustomUndoStack(int size) {
		this.dataArr = new BuildCommandRecord[size];
		this.insertIndex = 0;
		this.size = size;
	}

	public BuildCommandRecord pop() throws NoCommandHistoryException {
		if(this.isEmpty()) {
			throw new NoCommandHistoryException("The stack is empty.");
		}
		
		insertIndex-=1;
		
		if(insertIndex < 0) {
			insertIndex = this.size-1;
		}
		
		BuildCommandRecord data = dataArr[insertIndex];
		dataArr[insertIndex] = null;
		return data;		 
	}

	public BuildCommandRecord peek() throws NoCommandHistoryException {
		if(this.isEmpty()) {
			throw new NoCommandHistoryException("The stack is empty.");
		}

		int peekIndex = insertIndex - 1;

		if(peekIndex < 0) {
			peekIndex = this.size-1;
		}

		return dataArr[peekIndex];
	}

	public void push(BuildCommandRecord data) {
		this.dataArr[this.insertIndex] = data;
		this.insertIndex+=1;
		if(this.insertIndex == size) {
			this.insertIndex = 0;
		}
	}
	
	public boolean isEmpty() {
		int indexToCheck = insertIndex-1;
		
		if(indexToCheck < 0)
			indexToCheck = this.size-1;

		return dataArr[indexToCheck] == null;
	}
	
}
