package com.jobportal.model;

import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.model.enums.SubscriptionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class Employer {

	private int employerId;

	private int userId;

	@Email(message = "Contact email must be valid")
	private String contactEmail;

	@Size(max = 20, message = "Contact number max length is 20")
	private String contactNumber;

	private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

	private SubscriptionType subscriptionType = SubscriptionType.FREE;

	private LocalDate premiumExpiry;

	private Integer lastPaymentId;

	private boolean isDeleted = false;

	// ======= GETTERS & SETTERS =======
	public int getEmployerId() {
		return employerId;
	}

	public void setEmployerId(int employerId) {
		this.employerId = employerId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
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
