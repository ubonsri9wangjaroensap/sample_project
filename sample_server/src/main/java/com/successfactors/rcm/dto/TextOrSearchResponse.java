package com.successfactors.rcm.dto;


public class TextOrSearchResponse {

    private String type;

    private String message;

    public TextOrSearchResponse() {
        this.type = "TEXT";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
