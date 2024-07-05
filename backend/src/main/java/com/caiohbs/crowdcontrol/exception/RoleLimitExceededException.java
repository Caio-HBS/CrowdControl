package com.caiohbs.crowdcontrol.exception;

public class RoleLimitExceededException extends RuntimeException {
    public RoleLimitExceededException(String message) {
        super(message);
    }
}
