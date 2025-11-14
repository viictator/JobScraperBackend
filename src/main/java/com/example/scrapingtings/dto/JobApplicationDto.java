package com.example.scrapingtings.dto;

import com.example.scrapingtings.model.JobApplication;

public class JobApplicationDto {
    private int id;
    private String content;
    private int jobId;

    // Constructors, getters, setters
    public JobApplicationDto() {}

    public JobApplicationDto(int id, String content, int jobId) {
        this.id = id;
        this.content = content;
        this.jobId = jobId;
    }

    public static JobApplicationDto fromEntity(JobApplication entity) {
        return new JobApplicationDto(
                entity.getId(),
                entity.getContent(),
                entity.getJobId()
        );
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
}

