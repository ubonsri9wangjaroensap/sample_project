package com.successfactors.rcm.dto.dao;

public class JobRequistionInfor {
        String jobReqId;

        String jobTitle;

        public JobRequistionInfor(String jobReqId, String jobTitle){
            this.jobReqId = jobReqId;
            this.jobTitle = jobTitle;
        }

        public String getJobReqId() {
            return jobReqId;
        }

        public void setJobReqId(String jobReqId) {
            this.jobReqId = jobReqId;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobReqTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }
        
}
