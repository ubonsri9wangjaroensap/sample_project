package com.successfactors.rcm.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.successfactors.rcm.dto.*;
import com.successfactors.rcm.dto.JobList.JobListRequest;
import com.successfactors.rcm.dto.applytojob.ApplyToJob;
import com.successfactors.rcm.dto.feedback.Feedback;
import com.successfactors.rcm.dto.jobsearch.JobSearch;
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
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/talk")
@CrossOrigin(origins = "*", maxAge = 3600)
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
        System.out.println(jobReqSearchAreaDto.getLocale() + ""+jobReqSearchAreaDto.getTitle());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        JobSearch responseOBject = objectMapper.readValue(responseAsString, JobSearch.class);
        //String hardcoded = "https://qaautocand-api.lab-rot.ondemand.com/odata/v2/JobRequisitionLocale?$filter=externalTitle%20eq%20'402'%20and%20defaultLanguage%20eq%20'en_US'";
                    URL urlJobReqQuery = new URL(buildJobReqQueryUrl(jobReqSearchAreaDto));
                    try {
                        HttpURLConnection conn = (HttpURLConnection) urlJobReqQuery.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization", "Basic YWRtaW5iMUBSQ01FQzExNDJIYW5hOnB3ZA==");
                        conn.connect();
                        System.out.println("start connect "  + conn.getContent());
                        System.out.println(conn.getErrorStream());
                        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                           // response.setMessage("Here are the search results");
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder output = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                output.append(line + "\n");
                            }
                            br.close();
                            System.out.println(output.toString());
                            // the part that read out the gson object
//                            Gson gson = new Gson();
//                            String jsonObject = gson.toJson(output.toString());
//                            System.out.println(output.toString());
//                            List<JobRequistionInfor> response1 = new ObjectMapper().readValue(jsonObject ,new TypeReference<List<JobRequistionInfor>>(){});
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
        AbstractHelpResponse response;
        String type = request.getType();
        try {
            switch (TalkTypeEnum.valueOf(type)) {
                case TEXT:
                    String key = nlp.getKey(request.getData());
                    String responseAsString = jedis.hget(key, "RESPONSE");
                    String responseType = jedis.hget(key, "TYPE").replace("\\", "");

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

                    if (responseType.equals("FEEDBACK")) {
                        Feedback responseObj = objectMapper.readValue(responseAsString.replace("\\", ""), Feedback.class);
                        System.out.println(responseAsString);
                        return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
                    }

                    break;
                default:
                    return new ResponseEntity<>("Did not recognize request type " + request.getType(), HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid request type " + request.getType(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("asdsads", HttpStatus.CREATED);
    }

    @PostMapping("/applyToJob")
    public ResponseEntity applyToJob(@RequestBody ApplyToJob request) throws IOException {
        URL url = new URL("https://qaautocand-api.lab-rot.ondemand.com/odata/v2/upsert?$format=json");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Basic YWRtaW5iMUBSQ01FQzExNDJIYW5hOnB3ZA==");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        osw.write(buildJobApplyRequestBody(request.getData()));
        osw.flush();
        osw.close();
        os.close();
        conn.connect();
        int responseCode = conn.getResponseCode();
        System.out.println(conn.getResponseMessage());
        if (responseCode != 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
            }
        }
        conn.disconnect();

        return new ResponseEntity<>(responseCode == 200 ? "You have submitted the application! :)" : "Unable to submit application :(", HttpStatus.CREATED);
    }

    private String buildJobApplyRequestBody(Map<String, String> jobAppDetails) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"__metadata\": {")
                .append("\"uri\": \"JobApplication\",")
                .append("\"type\": \"SFOData.JobApplication\"")
                .append("},")
                .append("\"candidateId\": \"").append(getCandidateId(jobAppDetails.get("email"))).append("\",")
                .append("\"jobReqId\": \"").append(jobAppDetails.get("jobReqId")).append("\",")
                .append("\"contactEmail\": \"lebron.james@sapcom\"")
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
            if(!jobReqSearchAreaDto.getLocale().equals(null)){
                sb.append(hasFielterBefore ? "%20and%20": "");
                sb.append("locale" + "%20eq%20" +"\'" + jobReqSearchAreaDto.getLocale()+ "\'");
                hasFielterBefore = true;
            }
//            if(!jobReqSearchAreaDto.getCountry().equals(null)){
//                sb.append(hasFielterBefore ? "%20and%20": "");
//                sb.append("country" + "%20eq%20" + "\'" + jobReqSearchAreaDto.getCountry() + "\'");
//            }
//            if(!jobReqSearchAreaDto.getState().equals(null)){
//                sb.append(hasFielterBefore ? "%20and%20": "");
//                sb.append("state" + "%20eq%20" + "\'" + jobReqSearchAreaDto.getState() + "\'");
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

}
