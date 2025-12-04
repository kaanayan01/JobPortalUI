package com.jobportal.repository;

import com.jobportal.model.Subscription;
import java.util.List;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Subscription findById(int subscriptionId);
    List<Subscription> findAll();
    void update(Subscription subscription);
    void delete(int subscriptionId);
}
