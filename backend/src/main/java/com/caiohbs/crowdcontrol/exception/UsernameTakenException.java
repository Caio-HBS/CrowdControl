package com.caiohbs.crowdcontrol.exception;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException() {
        super("Username already taken");
    }
}
