package com.jobportal.repository;

import com.jobportal.model.ActivityLog;
import java.util.List;

public interface ActivityLogRepository {
    ActivityLog save(ActivityLog log);
    ActivityLog findById(int logId);
    List<ActivityLog> findAll();
    List<ActivityLog> findByUserId(int userId);
    void delete(int logId);
}
