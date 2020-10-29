package com.successfactors.rcm.controller;

import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/train")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TrainingController {

    @Autowired
    private Jedis jedis;

    private String RESPONSE = "RESPONSE";

    private String TYPE = "TYPE";

    @PostMapping
    public ResponseEntity train(@RequestBody String request) {
        try {
            JSONObject jsonObject = new JSONObject(request);
            System.out.println(jsonObject.getString("key"));
            JSONObject jsonObj = jsonObject.getJSONObject("response");
            jedis.set(jsonObject.getString("key"),jsonObj.toString());
            System.out.println(jsonObj.toString());
        } catch (JSONException err){
            System.out.println("Error");
        }

        return new ResponseEntity<>("Training data has been added to the model", HttpStatus.CREATED);
    }

}
