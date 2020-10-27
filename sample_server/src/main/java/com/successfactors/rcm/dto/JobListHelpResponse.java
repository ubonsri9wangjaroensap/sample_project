package com.successfactors.rcm.dto;

import com.successfactors.rcm.dto.dao.JobRequistionInfor;

import java.util.List;

public class JobListHelpResponse extends AbstractHelpResponse{

    public List<JobRequistionInfor> getData() {
        return data;
    }

    public void setData(List<JobRequistionInfor> jobRequisitions) {
        this.data = jobRequisitions;
    }

    private List<JobRequistionInfor> data;



}
