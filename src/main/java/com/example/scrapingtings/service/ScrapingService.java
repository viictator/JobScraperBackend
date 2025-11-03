package com.example.scrapingtings.service;

import com.example.scrapingtings.model.ScrapingJob;
import com.example.scrapingtings.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrapingService {

    /*private final JobRepository jobRepository;

    public ScrapingService (JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }*/

    @Autowired
    JobRepository jobRepository;


    public ResponseEntity<Void> receiveJobs (List<ScrapingJob> data) {
        data.forEach((job) -> {
            try {
                jobRepository.save(job);
                System.out.println("✅ Saved: " + job.getJobTitle());
            } catch (Exception e) {
                // Log the exact job and error that failed
                System.err.println("❌ ERROR saving job: " + job.getJobTitle() + " from " + job.getOriginsite());
                e.printStackTrace(); // This is essential for the stack trace
                // You can re-throw the exception here if you want the 500 status to remain
                throw new RuntimeException("Failed to save job: " + job.getJobTitle(), e);
            }
        });

        System.out.println("Received " + data.size() + " items");
        return ResponseEntity.ok().build();
    }



}
