package com.successfactors.rcm.dto;

import com.successfactors.rcm.util.TalkTypeEnum;

public class ApplyToJobHelpResponse extends AbstractHelpResponse {

    public ApplyToJobHelpResponse() {
        super.setType(TalkTypeEnum.APPLY_TO_JOB.toString());
    }

}
