package com.example.scrapingtings.controller;

import com.example.scrapingtings.model.ScrapingJob;
import com.example.scrapingtings.service.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/user/jobs")
@CrossOrigin(origins = "http://localhost:3000")
public class JobController {

    @Autowired
    ScrapingService scrapingService;


    @GetMapping("")
    public ResponseEntity<List<ScrapingJob>> getScrapedJobs() {
        return scrapingService.getAllJobs();
    }

    @GetMapping("/{jobid}")
    public ResponseEntity<ScrapingJob> getSpecificJob(@PathVariable int jobid) {
        return scrapingService.getSpecificJob(jobid);
    }

}


