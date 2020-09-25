package com.successfactors.rcm.request;

public class HelpRequest {
    private String message;

    public HelpRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
