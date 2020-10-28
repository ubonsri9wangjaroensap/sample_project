package com.successfactors.rcm.dto;

import com.successfactors.rcm.util.TalkTypeEnum;

public class JobListHelpResponse extends AbstractHelpResponse {

    public JobListHelpResponse() {
        super.setType(TalkTypeEnum.JOB_LIST.toString());
    }

}
