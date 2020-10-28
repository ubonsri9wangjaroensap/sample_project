package com.successfactors.rcm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.successfactors.rcm.dto.FeedbackTrainRequest;
import com.successfactors.rcm.dto.TrainRequest;
import com.successfactors.rcm.util.TalkTypeEnum;
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

    @PostMapping
    public ResponseEntity train(@RequestBody TrainRequest request)  {
        Gson gson = new Gson();

        if (request.getKey() != null) {
            jedis.set(request.getKey(), gson.toJson(request.getResponse()));
        }

        System.out.println(request.getKey());
        System.out.println(jedis.get(request.getKey()));


        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
