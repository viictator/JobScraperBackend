package com.example.scrapingtings.service;

import com.example.scrapingtings.Utils.DateUtils;
import com.example.scrapingtings.model.ScrapingJob;
import com.example.scrapingtings.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                if (!jobRepository.existsByJobTitleAndCompanyName(job.getJobTitle(), job.getCompanyName())) {
                    job.setTime(DateUtils.toDaysAgo(job.getTime()));
                    jobRepository.save(job);
                    System.out.println("✅ Saved: " + job.getJobTitle());
                } else {
                    System.out.println("❌ Skipped due to duplicate: " + job.getJobTitle() + "" + job.getCompanyName());
                }

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

    public ResponseEntity<List<ScrapingJob>> getAllJobs() {
        return new ResponseEntity<>(jobRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<ScrapingJob> getSpecificJob(int jobId) {
        ScrapingJob job = jobRepository.findById(jobId).
                orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Job not found, ID: " + jobId
        ));

        return new ResponseEntity<>(job, HttpStatus.OK);

    }



}
