package com.example.scrapingtings.controller;

import com.example.scrapingtings.dto.JobAppUpdateRequest;
import com.example.scrapingtings.dto.JobApplicationDto;
import com.example.scrapingtings.dto.JobDetailsRequest;
import com.example.scrapingtings.model.JobApplication;
import com.example.scrapingtings.model.ScrapingJob;
import com.example.scrapingtings.model.User;
import com.example.scrapingtings.repository.UserRepository;
import com.example.scrapingtings.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/jobapp")
@CrossOrigin(origins = "http://localhost:3000")
public class JobApplicationController {

    @Autowired
    JobApplicationService jobApplicationService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> generateApplication(
            @RequestBody JobDetailsRequest request, Principal principal) {

        try {
            String username = principal.getName();
            JobApplication newJobApp = jobApplicationService.generateApplication(request.id(), username);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Application generated successfully");
            responseBody.put("data", newJobApp); // ⭐ FULL OBJECT

            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);

        } catch(Exception e) {
            System.err.println("Error generating application: " + e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Failed to generate application");
            errorBody.put("details", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping("generateAll")
    public ResponseEntity<Map<String, Object>> generateAllApplications(Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
            String username = principal.getName();

            // Fetch user
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    ));

            // Validate profile completeness
            if (user.getPersonalName() == null ||
                    user.getPersonalEmail() == null ||
                    user.getPersonalAddress() == null ||
                    user.getProfileText() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Your profile is incomplete. Please fill out your name, email, address, and profile text before generating applications."
                );
            }

            // Generate all applications
            List<JobApplication> jobApplications = jobApplicationService.generateAllApplications(username);

            response.put("message", "Successfully generated applications");
            response.put("count", jobApplications.size());

            System.out.println("✅ Generated " + jobApplications.size() + " applications.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ResponseStatusException e) {
            // Controlled expected errors
            response.put("error", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(response);

        } catch (Exception e) {
            // Unexpected errors
            response.put("error", "Failed to generate applications");
            response.put("details", e.getMessage());
            System.err.println("❌ Error generating applications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> getSpecificJobApplication(Principal principal, @PathVariable int jobId) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with username " + username + " not found."
                ));

        try {
            List<JobApplication> jobApplications = jobApplicationService.getSpecificJobApplication(user.getId(), jobId);

            // Optional: log each received application
            jobApplications.forEach(jobApp -> {
                System.out.println("✅ Successfully received job application with ID: " + jobApp.getId());
            });

            // Create a JSON-friendly response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", jobApplications.size());
            response.put("data", jobApplications);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error getting job application: " + e.getMessage());

            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("success", false);
            errorResp.put("message", e.getMessage());

            return new ResponseEntity<>(errorResp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/{appId}")
    public ResponseEntity<JobApplicationDto> updateJobApplication(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable int appId,
            @Valid @RequestBody JobAppUpdateRequest request) {

        // Call service to handle the update logic
        JobApplication updatedApp = jobApplicationService.updateContent(principal.getUsername(), appId, request);

        // Return DTO
        return ResponseEntity.ok(JobApplicationDto.fromEntity(updatedApp));
    }






}
