package com.example.scrapingtings;

import com.example.scrapingtings.Model.ScrapingBook;
import com.example.scrapingtings.Model.ScrapingJob;
import com.example.scrapingtings.Utils.DateUtils;
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

    // Temporary in-memory storage
    private final List<ScrapingBook> scrapedBooks = new ArrayList<>();
    private final List<ScrapingJob> scrapedJobs = new ArrayList<>();


    @PostMapping("/scraped-books")
    public ResponseEntity<Void> receiveBooks(@RequestBody List<ScrapingBook> data) {
        scrapedBooks.addAll(data);
        System.out.println("Received " + data.size() + " items");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scraped-books")
    public ResponseEntity<List<ScrapingBook>> getScrapedBooks() {
        return ResponseEntity.ok(scrapedBooks);
    }

    @PostMapping("/scraped-jobs")
    public ResponseEntity<Void> receiveJobs(@RequestBody List<ScrapingJob> data) {
        for (ScrapingJob job : data) {
            job.setTime(DateUtils.toDaysAgo(job.getTime()));
        }
        scrapedJobs.clear();
        scrapedJobs.addAll(data);
        System.out.println("Received " + data.size() + " items");
        return ResponseEntity.ok().build();
    }


    @GetMapping("/scraped-jobs")
    public ResponseEntity<List<ScrapingJob>> getScrapedJobs() {
        return ResponseEntity.ok(scrapedJobs);
    }

    @PostMapping("/start-scraper")
    public ResponseEntity<String> startScraper() {
        try {
            // Adjust python executable and path to main.py as needed
            ProcessBuilder pb = new ProcessBuilder("C:\\Users\\victo\\AppData\\Local\\Programs\\Python\\Python313\\python.exe", "C:\\Users\\victo\\IdeaProjects\\JobScraperBackend\\src\\scraper\\main.py");

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
