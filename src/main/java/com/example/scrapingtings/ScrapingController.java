package com.example.scrapingtings;

import com.example.scrapingtings.Model.ScrapingModel;
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
    private final List<ScrapingModel> scrapedData = new ArrayList<>();

    @PostMapping("/scraped-data")
    public ResponseEntity<Void> receiveData(@RequestBody List<ScrapingModel> data) {
        scrapedData.addAll(data);
        System.out.println("Received " + data.size() + " items");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scraped-data")
    public ResponseEntity<List<ScrapingModel>> getScrapedData() {
        return ResponseEntity.ok(scrapedData);
    }




}
