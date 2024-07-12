package com.caiohbs.crowdcontrol.exception;

public class NameTakenException extends RuntimeException {
    public NameTakenException(String message) {
        super(message);
    }
}
