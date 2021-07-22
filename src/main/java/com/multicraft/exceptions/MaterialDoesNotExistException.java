package com.multicraft.exception;

public class MaterialDoesNotExistException extends Exception {

    private static final long serialVersionUID = 0L;

    public MaterialDoesNotExistException(String message) {
        super(message);
    }

}
