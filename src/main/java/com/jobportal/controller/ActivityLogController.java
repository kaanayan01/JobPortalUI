package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.ActivityLog;
import com.jobportal.service.ActivityLogService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService service;

    public ActivityLogController(ActivityLogService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<ActivityLog>> create(@RequestBody ActivityLog log) {
        ActivityLog created = service.create(log);
        return ResponseBuilder.success(created, "Activity log created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<ActivityLog>> getById(@PathVariable int id) {
        ActivityLog log = service.getById(id);
        return ResponseBuilder.success(log, "Activity log retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<ActivityLog>>> getAll() {
        List<ActivityLog> logs = service.getAll();
        return ResponseBuilder.success(logs, "All activity logs retrieved successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GenericResponse<List<ActivityLog>>> getByUser(@PathVariable int userId) {
        List<ActivityLog> logs = service.getByUserId(userId);
        return ResponseBuilder.success(logs, "Activity logs for user retrieved successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseBuilder.success(null, "Activity log deleted successfully");
    }
}
