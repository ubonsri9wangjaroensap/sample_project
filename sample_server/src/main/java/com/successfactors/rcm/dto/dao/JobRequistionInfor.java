package com.successfactors.rcm.dto.dao;

public class JobRequistionInfor {
        String jobReqId;

        String jobReqTitle;

        public JobRequistionInfor(String jobReqId, String jobReqTitle){
            this.jobReqId = jobReqId;
            this.jobReqTitle = jobReqTitle;
        }

        public String getJobReqId() {
            return jobReqId;
        }

        public void setJobReqId(String jobReqId) {
            this.jobReqId = jobReqId;
        }

        public String getJobReqTitle() {
            return jobReqTitle;
        }

        public void setJobReqTitle(String jobReqTitle) {
            this.jobReqTitle = jobReqTitle;
        }
        
}
