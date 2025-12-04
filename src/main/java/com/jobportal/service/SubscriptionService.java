package com.jobportal.service;

import com.jobportal.model.Subscription;
import com.jobportal.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;

    public SubscriptionService(SubscriptionRepository repository) {
        this.repository = repository;
    }

    public Subscription create(Subscription subscription) {
        return repository.save(subscription);
    }

    public Subscription getById(int id) {
        return repository.findById(id);
    }

    public List<Subscription> getAll() {
        return repository.findAll();
    }

    public void update(Subscription subscription) {
        repository.update(subscription);
        
    }

    public void delete(int subscriptionId) {
        repository.delete(subscriptionId);
    }
}
