package com.jobportal.service;

import com.jobportal.model.ActivityLog;
import com.jobportal.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public ActivityLogService(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public ActivityLog create(ActivityLog log) {
        return repository.save(log);
    }

    public ActivityLog getById(int id) {
        return repository.findById(id);
    }

    public List<ActivityLog> getAll() {
        return repository.findAll();
    }

    public List<ActivityLog> getByUserId(int userId) {
        return repository.findByUserId(userId);
    }

    public void delete(int logId) {
        repository.delete(logId);
    }
}
