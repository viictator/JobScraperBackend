package com.example.scrapingtings.controller;

import com.example.scrapingtings.dto.JobDetailsRequest;
import com.example.scrapingtings.model.JobApplication;
import com.example.scrapingtings.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobApplicationController {

    @Autowired
    JobApplicationService jobApplicationService;

    @PostMapping("generate")
    public ResponseEntity<String> generateApplication(@RequestBody JobDetailsRequest request) {

        try {

            JobApplication newJobApp = jobApplicationService.generateApplication(request.title(), request.company(), request.description());

            String successMessage = "Application generated and saved successfully with ID: " + newJobApp.getId() + " Title: " + newJobApp.getJobTitle();
            return new ResponseEntity<>(successMessage, HttpStatus.CREATED);

        } catch(Exception e) {
            System.err.println("Error generating application: " + e.getMessage());
            return new ResponseEntity<>("Failed to generate and save application: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
