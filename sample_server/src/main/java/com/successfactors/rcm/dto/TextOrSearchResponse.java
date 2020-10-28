package com.successfactors.rcm.dto;

public class TextOrSearchResponse extends AbstractHelpResponse{

    public static String REGULAR_TEXT_RESPONSE = "Sure, let see what I can do for you.";

    public static String REGULAR_JOB_SERACH_RESPONSE = "Please provide search criteria below";

    public void setRegularTextResponse(){
       setMessage(REGULAR_TEXT_RESPONSE);
    }

    public void setRegularJobSerachResponse(){
        setMessage(REGULAR_JOB_SERACH_RESPONSE);
    }

}
