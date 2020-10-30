package com.successfactors.rcm.dto.applytojob;

public class ApplyToJobResponse {

    private String type;

    private String message;

    public ApplyToJobResponse() {
        this.type = "jobApplication";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
