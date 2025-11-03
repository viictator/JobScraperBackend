package com.example.scrapingtings.model;

import jakarta.persistence.*;

@Entity
public class ScrapingJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String companyName;
    private String jobTitle;
    private String link;
    private String time;
    private String location;
    private String contract;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String originsite;



   /* public ScrapingJob(String companyName, String jobTitle, String link, String time, String location, String contract, String description, String originsite) {
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.link = link;
        this.time = time;
        this.location = location;
        this.contract = contract;
        this.description = description;
        this.originsite = originsite;
    }*/

    public ScrapingJob() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginsite() {
        return originsite;
    }

    public void setOriginsite(String originsite) {
        this.originsite = originsite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
