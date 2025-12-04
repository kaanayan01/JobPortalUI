package com.jobportal.model;

import java.time.LocalDateTime;

public class SavedJob {
    private int savedId;
    private int jobSeekerId;
    private int jobId;
    private LocalDateTime savedDate;

    // Getters and Setters
    public int getSavedId() { return savedId; }
    public void setSavedId(int savedId) { this.savedId = savedId; }

    public int getJobSeekerId() { return jobSeekerId; }
    public void setJobSeekerId(int jobSeekerId) { this.jobSeekerId = jobSeekerId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public LocalDateTime getSavedDate() { return savedDate; }
    public void setSavedDate(LocalDateTime savedDate) { this.savedDate = savedDate; }
}
