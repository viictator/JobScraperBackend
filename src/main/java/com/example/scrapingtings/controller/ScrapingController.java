package com.example.scrapingtings.controller;

import com.example.scrapingtings.model.ScrapingJob;
import com.example.scrapingtings.service.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ScrapingController {

    @Autowired
    ScrapingService scrapingService;


    // Temporary in-memory storage
    private final List<ScrapingJob> scrapedJobs = new ArrayList<>();

    @PostMapping("/scraped-jobs")
    public ResponseEntity<Void> receiveJobs(@RequestBody List<ScrapingJob> data) {
        /*for (ScrapingJob job : data) {
            job.setTime(DateUtils.toDaysAgo(job.getTime()));
        }
        scrapedJobs.clear();
        scrapedJobs.addAll(data);*/

        scrapingService.receiveJobs(data);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/scraped-jobs")
    public ResponseEntity<List<ScrapingJob>> getScrapedJobs() {
        return scrapingService.getAllJobs();
    }

    @PostMapping("/start-scraper")
    public ResponseEntity<String> startScraper() {
        try {
            // Adjust python executable and path to main.py as needed
            ProcessBuilder pb = new ProcessBuilder("C:\\Users\\Victor\\AppData\\Local\\Programs\\Python\\Python313\\python.exe", "C:\\Users\\Victor\\Desktop\\IntelliJ Projects\\JobScraperBackend\\src\\scraper\\main.py");

            Process process = pb.start();


            // Optional: Read output or error streams to log or debug
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.err.println(s);
            }

            // You can choose whether to wait for it to finish or not
            // process.waitFor();

            return ResponseEntity.ok("Scraper started successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to start scraper: " + e.getMessage());
        }
    }




}
