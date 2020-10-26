package com.successfactors.rcm.controller;

import com.successfactors.rcm.dto.AbstractHelpResponse;
import com.successfactors.rcm.dto.ApplyToJobHelpResponse;
import com.successfactors.rcm.dto.FeedbackHelpResponse;
import com.successfactors.rcm.dto.HelpRequest;
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
import java.net.URL;
import java.util.Base64;

@RestController
@RequestMapping("/talk")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HelpController {

    private final String BIZX_URL_PREFIX = "http://localhost:8080";

    String userCredentials = "cgrant:demo101";
    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

    @Autowired
    private Jedis jedis;

    @PostMapping
    public ResponseEntity askForHelp(@RequestBody HelpRequest request) throws MalformedURLException {
        AbstractHelpResponse response = null;
        String type = request.getType();
        try {
            switch (TalkTypeEnum.valueOf(type)) {
                case TEXT:
                    // Emmy TODO
                    break;
                case JOB_SEARCH:
                    // Emmy TODO
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

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
