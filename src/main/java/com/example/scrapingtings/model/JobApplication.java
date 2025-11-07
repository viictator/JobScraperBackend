package com.example.scrapingtings.model;

import jakarta.persistence.*;


@Entity
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String jobTitle;
    private String companyName;
    @Column(columnDefinition = "TEXT")
    private String content;

    public JobApplication() {
    }

    public JobApplication(String jobTitle, String companyName, String content) {
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String title) {
        this.jobTitle = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
