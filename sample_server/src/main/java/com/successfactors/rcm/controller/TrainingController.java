package com.successfactors.rcm.controller;

import com.google.gson.Gson;
import com.successfactors.rcm.dto.feedback.FeedbackTrain;
import com.successfactors.rcm.dto.jobsearch.JobSearchTrain;
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

    @PostMapping("/feedback")
    public ResponseEntity feedback(@RequestBody FeedbackTrain request)  {
        Gson gson = new Gson();

        if (request.getKey() != null) {
            jedis.set(request.getKey(), gson.toJson(request.getResponse()));
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/jobSearch")
    public ResponseEntity jobSearch(@RequestBody JobSearchTrain request)  {
        Gson gson = new Gson();

        if (request.getKey() != null) {
            jedis.set(request.getKey(), gson.toJson(request.getResponse()));
        }

        System.out.println(request.getKey());
        System.out.println(jedis.get(request.getKey()));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/applyToJob")
    public ResponseEntity applyToJob(@RequestBody FeedbackTrain request)  {
        Gson gson = new Gson();

        if (request.getKey() != null) {
            jedis.set(request.getKey(), gson.toJson(request.getResponse()));
        }

        System.out.println(request.getKey());
        System.out.println(jedis.get(request.getKey()));


        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
