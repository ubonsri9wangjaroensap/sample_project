package com.successfactors.rcm.response;

public class FeedbackResponse {
    private String message;

    public FeedbackResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
