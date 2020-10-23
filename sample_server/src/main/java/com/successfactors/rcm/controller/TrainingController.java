package com.successfactors.rcm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/train")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TrainingController {
    @PostMapping
    public ResponseEntity train() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
