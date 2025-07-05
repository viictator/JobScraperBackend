package com.example.scrapingtings;

import com.example.scrapingtings.Model.ScrapingBook;
import com.example.scrapingtings.Model.ScrapingJob;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
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
        scrapedJobs.addAll(data);
        System.out.println("Received " + data.size() + " items");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scraped-jobs")
    public ResponseEntity<List<ScrapingJob>> getScrapedJobs() {
        return ResponseEntity.ok(scrapedJobs);
    }




}
