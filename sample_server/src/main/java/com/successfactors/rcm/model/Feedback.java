package com.successfactors.rcm.model;

import com.successfactors.rcm.feedback.RatingEnum;

public class Feedback {
    private RatingEnum rating;

    private String message;

    public RatingEnum getRating() {
        return rating;
    }

    public void setRating(RatingEnum rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
