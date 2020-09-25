package com.successfactors.rcm.controller;

import org.springframework.web.bind.annotation.*;
import com.successfactors.rcm.request.HelpRequest;

@RestController
@RequestMapping("/help")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HelpController {
    @PostMapping()
    public String createHelp(@RequestBody HelpRequest request) {
        String textToParse = request.getMessage();
        return "hi";
    }
}
