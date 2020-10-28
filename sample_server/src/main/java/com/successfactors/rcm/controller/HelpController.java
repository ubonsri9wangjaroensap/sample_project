package com.successfactors.rcm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.successfactors.rcm.dto.*;
import com.successfactors.rcm.dto.dao.JobRequistionInfor;
import com.successfactors.rcm.util.NLP;
import com.successfactors.rcm.util.TalkTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/talk")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HelpController {

    private final String BIZX_URL_PREFIX = "http://localhost:8080";
    private final String TRAINING_SERVICE = "/train";

    String userCredentials = "cgrant:demo101";
    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

    @Autowired
    private Jedis jedis;

    @PostMapping
    public ResponseEntity askForHelp(@RequestBody HelpRequest request) throws MalformedURLException {
        AbstractHelpResponse response = null;
        JobListHelpResponse jobListHelpResponse = null;
        String type = request.getType();
        NLP nlp = new NLP();
        try {
            switch (TalkTypeEnum.valueOf(type)) {
                case TEXT:
                    String requestData = (String) request.getData();
                    String requestKey = nlp.getKey(requestData);
                    String responseFromNlp = jedis.get(requestKey);
                    response = new TextOrSearchResponse();
                    if (responseFromNlp == null) {
                      response.setMessage(TextOrSearchResponse.REGULAR_TEXT_RESPONSE);
                    } else {
                        response.setMessage(responseFromNlp);
                    }
                    response.setType(type);
                    break;
                case JOB_SEARCH:
                    // Emmy TODO
                    responseFromNlp = jedis.get(type);
                    response = new TextOrSearchResponse();
                    if (responseFromNlp == null) {
                       response.setMessage(TextOrSearchResponse.REGULAR_JOB_SERACH_RESPONSE);
                    } else {
                        response.setMessage(responseFromNlp);
                    }
                    response.setType(type);
                    break;
                case JOB_LIST:
                    // Emmy TODO
                    jobListHelpResponse = new JobListHelpResponse();
                    jobListHelpResponse.setType(type);
                    Gson gson = new Gson();
                    String object = gson.toJson(request.getData());
                    JobReqSearchAreaDto jobReqSearchAreaDto = null;
                    try {
                        jobReqSearchAreaDto = new ObjectMapper().readValue(object, JobReqSearchAreaDto.class);
                    } catch (JsonProcessingException e) {
                        return new ResponseEntity<>("Can not parse object from filter fields " + request.getType(), HttpStatus.BAD_REQUEST);
                    }
                    URL urlJobReqQuery = new URL(buildJobReqQueryUrl(jobReqSearchAreaDto));
                    try {
                        HttpURLConnection conn = (HttpURLConnection) urlJobReqQuery.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Authorization", basicAuth);
                        conn.connect();
                        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                            jobListHelpResponse.setMessage("Here are the search results");
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder output = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                output.append(line + "\n");
                            }
                            br.close();
                            String jsonObject = gson.toJson(output.toString());
                            List<JobRequistionInfor> response1 = new ObjectMapper().readValue(jsonObject ,new TypeReference<List<JobRequistionInfor>>(){});
                            jobListHelpResponse.setData(response1);
                        }else{
                            return new ResponseEntity<>("Connection Error, job requisition can not be found " + request.getType(), HttpStatus.BAD_REQUEST);
                        }
                        conn.disconnect();
                        } catch (ProtocolException protocolException) {
                        protocolException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    break;

                case FEEDBACK:
                    String responseMessage = jedis.get(type);
                    if (responseMessage == null) {
                        return new ResponseEntity<>("Sorry we did not understand you. ", HttpStatus.BAD_REQUEST);
                    }

                    response = new FeedbackHelpResponse();
                    response.setType("feedback");
                    response.setMessage(responseMessage);
                    ((FeedbackHelpResponse) response).setChoices(new String[]{"Poor", "Average", "Good"});
                    break;
                case APPLY_TO_JOB:
                    // Justin TODO
                    URL url = new URL(BIZX_URL_PREFIX + "/odata/v2/JobApplication");
                    try {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Authorization", basicAuth);
                        response.setMessage(conn.getResponseCode() == 200 ? "Your application has been sent!" : "Failed to apply :(");

                        InputStreamReader in = new InputStreamReader(conn.getInputStream());
                        BufferedReader br = new BufferedReader(in);
                        String output;
                        while ((output = br.readLine()) != null) {
                            System.out.println(output);
                        }
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    response = new ApplyToJobHelpResponse();
                    break;
                default:
                    return new ResponseEntity<>("Invalid request type " + request.getType(), HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid request type " + request.getType(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>( jobListHelpResponse == null ? response : jobListHelpResponse, HttpStatus.CREATED);
    }


    private String buildJobReqQueryUrl(JobReqSearchAreaDto jobReqSearchAreaDto){
        String urlJobReq = BIZX_URL_PREFIX + "/odata/v2/JobRequisition";
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        sb.append("title=").append(jobReqSearchAreaDto.getTitle()).append("&").append("city=").append(jobReqSearchAreaDto.getCity()).append("&");
        sb.append("state=").append(jobReqSearchAreaDto.getState()).append("&").append("country=").append(jobReqSearchAreaDto.getCountry());
        urlJobReq = urlJobReq + sb.toString();
        return urlJobReq;
    }

}