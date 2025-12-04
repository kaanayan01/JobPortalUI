package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.Application;
import com.jobportal.service.ApplicationService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Application>> create(@RequestBody Application a) {
        Application created = service.create(a);
        return ResponseBuilder.success(created, "Application created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Application>> getById(@PathVariable int id) {
        Application app = service.findById(id);
        return ResponseBuilder.success(app, "Application retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<Application>>> getAll() {
        List<Application> apps = service.findAll();
        return ResponseBuilder.success(apps, "All applications retrieved successfully");
    }

    @GetMapping("/jobseeker/{jobSeekerId}")
    public ResponseEntity<GenericResponse<List<Application>>> getByJobSeeker(@PathVariable int jobSeekerId) {
        List<Application> apps = service.findByJobSeekerId(jobSeekerId);
        return ResponseBuilder.success(apps, "Applications for job seeker retrieved successfully");
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<GenericResponse<List<Application>>> getByJob(@PathVariable int jobId) {
        List<Application> apps = service.findByJobId(jobId);
        return ResponseBuilder.success(apps, "Applications for job retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<Application>> update(@PathVariable int id, @RequestBody Application a) {
        a.setApplicationId(id);
        Application updated = service.update(a);
        return ResponseBuilder.success(updated, "Application updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.softDelete(id);
        return ResponseBuilder.success(null, "Application soft deleted successfully");
    }
}
