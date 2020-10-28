package com.successfactors.rcm.dto.JobList;

import com.successfactors.rcm.dto.JobReqSearchAreaDto;

public class JobListRequest {
    private JobReqSearchAreaDto  data;

    private String type;

    public JobReqSearchAreaDto getData() {
        return data;
    }

    public void setData(JobReqSearchAreaDto data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
