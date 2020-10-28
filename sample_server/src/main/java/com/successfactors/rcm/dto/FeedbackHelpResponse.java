package com.successfactors.rcm.dto;

import com.successfactors.rcm.util.TalkTypeEnum;

public class FeedbackHelpResponse extends AbstractHelpResponse {

    public FeedbackHelpResponse() {
        super.setType(TalkTypeEnum.FEEDBACK.toString());
    }

}
