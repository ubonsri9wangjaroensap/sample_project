package com.successfactors.rcm.dto;

public class TrainRequest {

    private String key;

    private FeedbackTrainRequest response;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FeedbackTrainRequest getResponse() {
        return response;
    }

    public void setResponse(FeedbackTrainRequest response) {
        this.response = response;
    }

}
