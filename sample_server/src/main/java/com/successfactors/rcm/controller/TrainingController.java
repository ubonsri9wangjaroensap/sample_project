package com.successfactors.rcm.controller;

import com.successfactors.rcm.dto.TrainRequest;
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
    public ResponseEntity train(@RequestBody TrainRequest request) {
        if (request.getType() != null) {
            jedis.set(request.getType(), request.getResponse());
        }

        if (request.getMessage() != null) {
            jedis.set(request.getMessage(), request.getResponse());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
