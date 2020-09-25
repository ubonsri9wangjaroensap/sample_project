package com.successfactors.rcm.controller;

import com.successfactors.rcm.feedback.RatingEnum;
import com.successfactors.rcm.model.Feedback;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.successfactors.rcm.request.FeedbackRequest;
import com.successfactors.rcm.response.FeedbackResponse;

@RestController
@RequestMapping("/feedback")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FeedbackController {

    private final String THANK_YOU_MSG = "Thank you for the feedback.";

    @PostMapping
    public ResponseEntity createFeedback(@RequestBody FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setRating(request.isLike() ? RatingEnum.THUMBS_UP : RatingEnum.THUMBS_DOWN);
        feedback.setMessage(request.getMessage());
        return new ResponseEntity(new FeedbackResponse(THANK_YOU_MSG), HttpStatus.CREATED);
    }
}
