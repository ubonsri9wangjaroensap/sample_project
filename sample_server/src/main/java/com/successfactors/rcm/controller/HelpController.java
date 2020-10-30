package com.successfactors.rcm.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.successfactors.rcm.dto.*;
import com.successfactors.rcm.dto.JobList.JobListRequest;
import com.successfactors.rcm.dto.applytojob.ApplyToJob;
import com.successfactors.rcm.dto.applytojob.ApplyToJobResponse;
import com.successfactors.rcm.dto.dao.JobDetail;
import com.successfactors.rcm.dto.feedback.Feedback;
import com.successfactors.rcm.dto.feedback.ThankYouForFeedback;
import com.successfactors.rcm.dto.jobsearch.JobSearch;
import com.successfactors.rcm.dto.jobsearch.JobSearchInputForm;
import com.successfactors.rcm.util.NLP;
import com.successfactors.rcm.util.TalkTypeEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/talk")
@CrossOrigin(origins = "http://localhost:3000")
public class HelpController {

    private final String BIZX_URL_PREFIX = "http://localhost:8080";

    String userCredentials = "adminb1@RCMEC1142Hana:pwd";
    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

    @Autowired
    private Jedis jedis;

    @Autowired
    private NLP nlp;

    @PostMapping("/jobReqSearch")
    public ResponseEntity jobReqSearch(@RequestBody JobListRequest request) throws JsonProcessingException, MalformedURLException {
        JobReqSearchAreaDto jobReqSearchAreaDto = request.getData();
        //response from jedis
        String responseAsString = jedis.get(request.getType());
        String key = request.getType();
        System.out.println("response from jedis" +responseAsString);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        JobSearch responseOBject = objectMapper.readValue(responseAsString, JobSearch.class);
                    URL urlJobReqQuery = new URL(buildJobReqQueryUrl(jobReqSearchAreaDto));
                    try {
                        HttpURLConnection conn = (HttpURLConnection) urlJobReqQuery.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization", "Basic YWRtaW5iMUBSQ01FQzExNDJIYW5hOnB3ZA==");
                        conn.connect();
                        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder output = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                output.append(line + "\n");
                            }
                            br.close();
                            JSONObject jsonObject = new JSONObject(output.toString());
                            System.out.println(jsonObject.toString());
                            JSONObject jsonObj = jsonObject.getJSONObject("d");
                            JSONArray listResult = jsonObj.getJSONArray("results");
                            System.out.println(jsonObj.toString());
                            List<JobDetail> responseData = new ArrayList<>();
                            for(int i = 0; i< listResult.length();i++){
                                JSONObject response = listResult.getJSONObject(i);
                                JobDetail data = new JobDetail();
                                data.setId(response.get("jobReqId").toString());
                                data.setTitle(response.get("jobTitle").toString());
                                responseData.add(data);
                            }
                            responseOBject.setData(responseData);
                            JSONObject returnValue = new JSONObject(responseOBject);
                            System.out.println(returnValue.toString());
                            jedis.set(request.getType(),returnValue.toString());

                        }else{
                            return new ResponseEntity<>("Connection Error, job requisition can not be found " + request.getType(), HttpStatus.BAD_REQUEST);
                        }
                        conn.disconnect();
                        } catch (ProtocolException protocolException) {
                        protocolException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
        return new ResponseEntity<>(responseOBject, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity askForHelp(@RequestBody HelpRequest request) throws IOException {
        String responseAsString;
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject;

        try {
            switch (TalkTypeEnum.valueOf(request.getType())) {
                case TEXT:
                    responseAsString = jedis.get(nlp.getKey(request.getData()));

                    System.out.println(nlp.getKey(request.getData()));
                    System.out.println(responseAsString);

                    if (responseAsString == null) {
                        TextOrSearchResponse response = new TextOrSearchResponse();
                        response.setMessage("We did not understand your request");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }

                    jsonObject = new JSONObject(responseAsString);
                    String responseType = jsonObject.getString("type");

                    if (responseType.equals("FEEDBACK")) {
                        Feedback responseObj = objectMapper.readValue(responseAsString, Feedback.class);
                        System.out.println(responseAsString);
                        return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
                    } else if (responseType.equals("JOB_SEARCH")) {
                        JobSearchInputForm responseObj = objectMapper.readValue(responseAsString, JobSearchInputForm.class);
                        System.out.println(responseAsString);
                        return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
                    } else {
                        TextOrSearchResponse responseObj = new TextOrSearchResponse();
                        responseObj.setMessage("We don't understand your message :(");
                        return new ResponseEntity(responseObj, HttpStatus.CREATED);
                    }

                case FEEDBACK:
                    responseAsString = jedis.get(request.getType());
                    if (responseAsString != null) {
                        ThankYouForFeedback responseObj = objectMapper.readValue(responseAsString, ThankYouForFeedback.class);
                        return new ResponseEntity<>(responseObj, HttpStatus.OK);
                    }
                default:
                    TextOrSearchResponse response = new TextOrSearchResponse();
                    response.setMessage("Did not recognize request type " + request.getType());
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            TextOrSearchResponse response = new TextOrSearchResponse();
            response.setMessage("Invalid request type " + request.getType());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/applyToJob")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity applyToJob(@RequestBody ApplyToJob request) throws IOException {
        URL url = new URL("https://qaautocand-api.lab-rot.ondemand.com/odata/v2/upsert?$format=json");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Basic YWRtaW5iMUBSQ01FQzExNDJIYW5hOnB3ZA==");
        conn.setDoOutput(true);

        System.out.println("Inside applyToJob");

        OutputStream os = conn.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        osw.write(buildJobApplyRequestBody(request.getData()));
        osw.flush();
        osw.close();
        os.close();
        conn.connect();
        int responseCode = conn.getResponseCode();
        System.out.println("JobApplication OData, response is " + conn.getResponseCode());
        System.out.println(conn.getResponseMessage());
        if (responseCode != 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
            }
        } else {
            StringBuilder requestBody = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
                requestBody.append(strCurrentLine);
            }
            System.out.println(requestBody.toString());
        }
        conn.disconnect();

        ApplyToJobResponse response = new ApplyToJobResponse();
        response.setMessage(responseCode == 200 ? "Success" : "Unable to submit application :(");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private String buildJobApplyRequestBody(Map<String, String> jobAppDetails) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"__metadata\": {")
                .append("\"uri\": \"JobApplication\",")
                .append("\"type\": \"SFOData.JobApplication\"")
                .append("},")
                .append("\"candidateId\": \"").append(getCandidateId(jobAppDetails.get("username"))).append("\",")
                .append("\"jobReqId\": \"").append(jobAppDetails.get("jobReqId")).append("\"")
                .append("}");
        return sb.toString();
    }

    private String buildJobReqQueryUrl(JobReqSearchAreaDto jobReqSearchAreaDto){
        String urlJobReq = "https://qaautocand-api.lab-rot.ondemand.com/odata/v2/JobRequisitionLocale";
        StringBuilder sb = new StringBuilder();
        Boolean hasFielterBefore = false;
        if (jobReqSearchAreaDto != null){
            sb.append("?$filter=");
            if(!jobReqSearchAreaDto.getTitle().equals(null)){
                sb.append("externalTitle" + "%20eq%20" + "\'"+jobReqSearchAreaDto.getTitle()+"\'");
                hasFielterBefore = true;
            }
//            if(!jobReqSearchAreaDto.getCity().equals(null)){
//                sb.append(hasFielterBefore ? "%20and%20": "");
//                sb.append("locale" + "%20eq%20" +"\'" + jobReqSearchAreaDto.getCity()+ "\'");
//                hasFielterBefore = true;
//            }
        }
        urlJobReq = urlJobReq + sb.toString();
        System.out.println(urlJobReq);
        return urlJobReq;
    }

    private String getCandidateId(String email) throws IOException {
        URL url = new URL("https://qaautocand-api.lab-rot.ondemand.com/odata/v2/Candidate?$format=json&$select&$filter=contactEmail%20eq%20'" + email + "'");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", basicAuth);
        conn.connect();
        StringBuilder requestBody = new StringBuilder();
        System.out.println("Candidate OData, response is " + conn.getResponseCode());
        if (conn.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
                requestBody.append(strCurrentLine);
            }
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
            }
        }
        conn.disconnect();
        System.out.println(requestBody.toString());
        JSONObject jsonObject = new JSONObject(requestBody.toString());
        JSONArray results = jsonObject.getJSONObject("d").getJSONArray("results");
        JSONObject candidate = (JSONObject) results.get(0);

        return (String) candidate.get("candidateId");
    }

    @RequestMapping(
            value = "/**",
            method = RequestMethod.OPTIONS
    )
    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }

}
