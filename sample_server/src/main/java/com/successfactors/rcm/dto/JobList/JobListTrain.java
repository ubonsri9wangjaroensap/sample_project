package com.successfactors.rcm.dto.JobList;


import com.successfactors.rcm.dto.jobsearch.JobSearch;

public class JobListTrain {
    private String key;

    private JobSearch response;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JobSearch getResponse() {
        return response;
    }

    public void setResponse(JobSearch response) {
        this.response = response;
    }
}
