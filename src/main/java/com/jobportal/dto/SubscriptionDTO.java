package com.jobportal.dto;

import java.math.BigDecimal;

import com.jobportal.model.enums.PlanType;

public class SubscriptionDTO {
    private int subscriptionId;
    private PlanType planType;
    private int duration; // in days
    private BigDecimal price;

    // Getters and Setters
    public int getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(int subscriptionId) { this.subscriptionId = subscriptionId; }

    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
