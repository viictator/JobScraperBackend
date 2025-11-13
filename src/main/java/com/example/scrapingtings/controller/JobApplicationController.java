package com.example.scrapingtings.controller;

import com.example.scrapingtings.dto.JobDetailsRequest;
import com.example.scrapingtings.model.JobApplication;
import com.example.scrapingtings.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user/jobapp")
@CrossOrigin(origins = "http://localhost:3000")
public class JobApplicationController {

    @Autowired
    JobApplicationService jobApplicationService;

    @PostMapping("")
    public ResponseEntity<String> generateApplication(@RequestBody JobDetailsRequest request, Principal principal) {

        try {
            String username = principal.getName();
            JobApplication newJobApp = jobApplicationService.generateApplication(request.id(), username);

            String successMessage = "Application generated and saved successfully with ID: " + newJobApp.getId() + " Title: " + newJobApp.getJobTitle();
            return new ResponseEntity<>(successMessage, HttpStatus.CREATED);

        } catch(Exception e) {
            System.err.println("Error generating application: " + e.getMessage());
            return new ResponseEntity<>("Failed to generate and save application: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("generateAll")
    public ResponseEntity<String> generateAllApplications(Principal principal) {
        try {
            String username = principal.getName();
            List<JobApplication> jobApplications = jobApplicationService.generateAllApplications(username);
            String successMessage =
                    "Successfully generated " + jobApplications.size() + " job applications";
            System.out.println("✅ " + successMessage);
            return new ResponseEntity<>(successMessage, HttpStatus.CREATED);

        } catch(Exception e) {
            System.err.println("Error generating ALL applications " + e.getMessage());
            return new ResponseEntity<>("Failed to generate ALL and save applications " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }



}
