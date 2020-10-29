package com.successfactors.rcm.dto.feedback;

import java.util.Map;

public class ThankYouForFeedback {

    private String key;

    private Map<String, String> response;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getResponse() {
        return response;
    }

    public void setResponse(Map<String, String> response) {
        this.response = response;
    }

}
