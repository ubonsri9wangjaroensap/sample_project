package com.successfactors.rcm.dto.jobsearch;

import com.successfactors.rcm.dto.dao.JobDetail;

import java.util.List;

public class JobSearch {

    private String type;

    private List<JobDetail> data;

    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<JobDetail> getData() {
        return data;
    }

    public void setData(List<JobDetail> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
