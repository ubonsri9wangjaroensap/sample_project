package com.successfactors.rcm.request;

public class FeedbackRequest {
    private boolean like;

    private String message;

    public FeedbackRequest(boolean like, String message) {
        this.like = like;
        this.message = message;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
