package com.successfactors.rcm.dto;

public class FeedbackHelpResponse extends AbstractHelpResponse {

    String[] choices;

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }
}
