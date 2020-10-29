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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/talk")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HelpController {

    private final String BIZX_URL_PREFIX = "http://localhost:8080";

    String userCredentials = "cgrant@demo101";
    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

    @Autowired
    private Jedis jedis;

    @Autowired
    private NLP nlp;

    @PostMapping("/jobReqSearch")
    public ResponseEntity jobReqSearch(@RequestBody JobListRequest request) throws JsonProcessingException {
        JobReqSearchAreaDto jobReqSearchAreaDto = request.getData();
        //response from jedis
        String responseAsString = jedis.get(request.getType());
        System.out.println(responseAsString);
        ObjectMapper objectMapper = new ObjectMapper();
        JobSearch responseOBject = objectMapper.readValue(responseAsString, JobSearch.class);
        return new ResponseEntity<>(responseOBject, HttpStatus.CREATED);

//                    try {
//                        jobReqSearchAreaDto = new ObjectMapper().readValue(object, JobReqSearchAreaDto.class);
//                    } catch (JsonProcessingException e) {
//                        return new ResponseEntity<>("Can not parse object from filter fields " + request.getType(), HttpStatus.BAD_REQUEST);
//                    }
//                    URL urlJobReqQuery = new URL(buildJobReqQueryUrl(jobReqSearchAreaDto));
//                    try {
//                        HttpURLConnection conn = (HttpURLConnection) urlJobReqQuery.openConnection();
//                        conn.setRequestMethod("GET");
//                        conn.setRequestProperty("Accept", "application/json");
//                        conn.setRequestProperty("Authorization", basicAuth);
//                        conn.connect();
//                        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
//                            response.setMessage("Here are the search results");
//                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                            StringBuilder output = new StringBuilder();
//                            String line;
//                            while ((line = br.readLine()) != null) {
//                                output.append(line + "\n");
//                            }
//                            br.close();
//                            String jsonObject = gson.toJson(output.toString());
//                            List<JobRequistionInfor> response1 = new ObjectMapper().readValue(jsonObject ,new TypeReference<List<JobRequistionInfor>>(){});
//                            response.setData(response1);
//                        }else{
//                            return new ResponseEntity<>("Connection Error, job requisition can not be found " + request.getType(), HttpStatus.BAD_REQUEST);
//                        }
//                        conn.disconnect();
//                        } catch (ProtocolException protocolException) {
//                        protocolException.printStackTrace();
//                    } catch (IOException ioException) {
//                        ioException.printStackTrace();
//                    }
//                    break;
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

//                    if(key.equals("done")){
//                        System.out.println(responseAsString);
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        Feedback responseObj = objectMapper.readValue(responseAsString, Feedback.class);
//                        return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
//                    } else if (key.equals("search_job")){
////                        System.out.println(responseAsString);
////                        ObjectMapper objectMapper = new ObjectMapper();
////                        JobSearch responseObj = objectMapper.readValue(responseAsString, JobSearch.class);
//                        //return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
//                        System.out.println(responseAsString);
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        JobSearchResponse responseObj = objectMapper.readValue(responseAsString, JobSearchResponse.class);
//                        return new ResponseEntity<>(responseObj, HttpStatus.CREATED);
//                    }  else{
//                        response = new TextOrSearchResponse();
//                        response.setType(TalkTypeEnum.TEXT.toString());
//                        response.setMessage( TextOrSearchResponse.REGULAR_TEXT_RESPONSE);
//                    }

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
//        URL url = new URL(BIZX_URL_PREFIX + "/odata/v2/JobApplication");

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

    private String buildJobApplyRequestBody(Map<String, String> jobAppDetails) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"__metadata\": {")
                .append("\"uri\": \"JobApplication(9724)\",")
                .append("\"type\": \"SFOData.JobApplication\"")
                .append("},")
                .append("\"candidateId\": \"").append(jobAppDetails.get("candidateId")).append("\",")
                .append("\"jobReqId\": \"").append(jobAppDetails.get("jobReqId")).append("\",")
                .append("\"contactEmail\": \"lebron.james@sapcom\"")
                .append("}");
        return sb.toString();
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
