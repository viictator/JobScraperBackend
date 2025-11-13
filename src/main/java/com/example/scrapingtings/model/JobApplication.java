package com.example.scrapingtings.model;

import jakarta.persistence.*;


@Entity
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int jobId;
    private int userId;
    private String jobTitle;
    private String companyName;
    @Column(columnDefinition = "TEXT")
    private String content;

    public JobApplication() {
    }

    public JobApplication(int jobId, int userId, String jobTitle, String companyName, String content) {
        this.jobId = jobId;
        this.userId = userId;
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

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
