package com.multicraft.exception;

public class NoCommandHistoryException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public NoCommandHistoryException(String message) {
		super(message);
	}

}
