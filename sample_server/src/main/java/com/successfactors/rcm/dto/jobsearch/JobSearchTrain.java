package com.successfactors.rcm.dto.jobsearch;

import com.successfactors.rcm.dto.dao.JobSearchResponse;

public class JobSearchTrain {

    private String key;

    private JobSearchResponse response;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JobSearchResponse getResponse() {
        return response;
    }

    public void setResponse(JobSearchResponse message) {
        this.response = message;
    }
}
