package com.successfactors.rcm.util;

public enum TalkTypeEnum {

    TEXT("text"),
    FEEDBACK("feedback"),
    APPLY_TO_JOB("jobApplication"),
    JOB_SEARCH("jobSearch");

    public final String type;
    TalkTypeEnum(String type) {
        this.type = type;
    }

}
