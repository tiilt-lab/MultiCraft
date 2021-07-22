package com.multicraft.exceptions;

public class NoCommandHistoryException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public NoCommandHistoryException(String message) {
		super(message);
	}

}
