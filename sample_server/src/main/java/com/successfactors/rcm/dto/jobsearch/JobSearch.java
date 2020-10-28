package com.successfactors.rcm.dto.jobsearch;

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

    private class JobDetail {
        private String id;

        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
