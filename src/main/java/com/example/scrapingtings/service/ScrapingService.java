package com.example.scrapingtings.service;

import com.example.scrapingtings.Utils.DateUtils;
import com.example.scrapingtings.model.ScrapingJob;
import com.example.scrapingtings.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScrapingService {

    @Autowired
    JobRepository jobRepository;


    @Transactional
    public ResponseEntity<Void> receiveJobs(List<ScrapingJob> data) {

        System.out.println("🗑 Clearing old job database...");
        jobRepository.deleteAll();

        System.out.println("💾 Saving new scraped jobs...");
        data.forEach(job -> {
            try {
                job.setTime(DateUtils.toDaysAgo(job.getTime()));
                jobRepository.save(job);

                System.out.println("✅ Saved: " + job.getJobTitle());

            } catch (Exception e) {
                System.err.println("❌ ERROR saving job: " + job.getJobTitle() + " from " + job.getOriginsite());
                e.printStackTrace();
                throw new RuntimeException("Failed to save job: " + job.getJobTitle(), e);
            }
        });

        System.out.println("🎉 Successfully replaced all jobs with " + data.size() + " new items");

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
