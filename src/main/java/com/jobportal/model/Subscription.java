package com.jobportal.model;

import java.math.BigDecimal;

import com.jobportal.model.enums.PlanType;
import com.jobportal.model.enums.UserType;

public class Subscription {
    private int subscriptionId;
    private PlanType planType;
    private int duration; // in days
    private BigDecimal price;
    private UserType userType;

    // Getters and Setters
    public int getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(int subscriptionId) { this.subscriptionId = subscriptionId; }

    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }
    

    public UserType getUserType() {
		return userType;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
