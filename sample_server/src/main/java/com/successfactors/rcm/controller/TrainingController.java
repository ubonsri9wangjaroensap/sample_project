package com.successfactors.rcm.controller;

import com.successfactors.rcm.dto.feedback.TrainRequest;
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
    public ResponseEntity train(@RequestBody TrainRequest request) {
        if (request.getKey() != null) {
            jedis.hset(request.getKey(), RESPONSE, request.getResponse());
            jedis.hset(request.getKey(), TYPE, request.getType());
        }

        System.out.println(jedis.hget(request.getKey(), RESPONSE));
        System.out.println(jedis.hget(request.getKey(), TYPE));

        return new ResponseEntity<>("Training data has been added to the model", HttpStatus.CREATED);
    }

}
