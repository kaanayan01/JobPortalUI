package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.Subscription;
import com.jobportal.service.SubscriptionService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Subscription>> create(@RequestBody Subscription subscription) {
        Subscription created = service.create(subscription);
        return ResponseBuilder.success(created, "Subscription created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Subscription>> getById(@PathVariable int id) {
        Subscription subscription = service.getById(id);
        return ResponseBuilder.success(subscription, "Subscription retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<Subscription>>> getAll() {
        List<Subscription> list = service.getAll();
        return ResponseBuilder.success(list, "All subscriptions retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<Subscription>> update(@PathVariable int id, @RequestBody Subscription subscription) {
        subscription.setSubscriptionId(id);
        service.update(subscription);
        return ResponseBuilder.success(null, "Subscription updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseBuilder.success(null, "Subscription deleted successfully");
    }    
}
