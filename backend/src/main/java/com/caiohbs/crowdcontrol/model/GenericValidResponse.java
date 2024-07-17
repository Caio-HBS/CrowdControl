package com.caiohbs.crowdcontrol.model;

public class GenericValidResponse {

    private String message;

    public GenericValidResponse() {}

    public GenericValidResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
