package com.successfactors.rcm.dto.jobsearch;

import com.successfactors.rcm.dto.feedback.Feedback;

public class JobSearchTrain {
    private String key;

    private Feedback response;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Feedback getResponse() {
        return response;
    }

    public void setResponse(Feedback response) {
        this.response = response;
    }
}
