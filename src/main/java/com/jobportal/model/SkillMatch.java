package com.jobportal.model;

import java.time.LocalDateTime;

public class SkillMatch {
    private int matchId;
    private int jobSeekerId;
    private int jobId;
    private double matchPercentage;
    private LocalDateTime calculatedDate;

    // Getters and Setters
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public int getJobSeekerId() { return jobSeekerId; }
    public void setJobSeekerId(int jobSeekerId) { this.jobSeekerId = jobSeekerId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(double matchPercentage) { this.matchPercentage = matchPercentage; }

    public LocalDateTime getCalculatedDate() { return calculatedDate; }
    public void setCalculatedDate(LocalDateTime calculatedDate) { this.calculatedDate = calculatedDate; }
}
