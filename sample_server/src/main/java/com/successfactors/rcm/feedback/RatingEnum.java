package com.successfactors.rcm.feedback;

public enum RatingEnum {
    THUMBS_UP(1),
    THUMBS_DOWN(0);

    int value;

    RatingEnum(int value) {
        this.value = value;
    }
}
