package com.jobportal.dto;

import com.jobportal.model.enums.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class JobSeekerDTO {

	private int jobSeekerId;

	@NotNull(message = "User ID is required")
	private Integer userId;

	@Size(max = 255, message = "Resume file path max length is 255")
	private String resumeFile;

	// Skills stored as JSON string
	private List<String> skills;

	@NotNull(message = "Subscription type is required")
	private SubscriptionType subscriptionType = SubscriptionType.FREE;

	private LocalDate premiumExpiry;

	private Integer lastPaymentId;

	private boolean isDeleted = false;

	// ======= GETTERS & SETTERS =======
	public int getJobSeekerId() {
		return jobSeekerId;
	}

	public void setJobSeekerId(int jobSeekerId) {
		this.jobSeekerId = jobSeekerId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getResumeFile() {
		return resumeFile;
	}

	public void setResumeFile(String resumeFile) {
		this.resumeFile = resumeFile;
	}

	

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public LocalDate getPremiumExpiry() {
		return premiumExpiry;
	}

	public void setPremiumExpiry(LocalDate premiumExpiry) {
		this.premiumExpiry = premiumExpiry;
	}

	public Integer getLastPaymentId() {
		return lastPaymentId;
	}

	public void setLastPaymentId(Integer lastPaymentId) {
		this.lastPaymentId = lastPaymentId;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}
}
