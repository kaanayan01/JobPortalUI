package com.jobportal.repository;

import com.jobportal.model.Application;
import java.util.List;

public interface ApplicationRepository {
    Application save(Application application);
    Application findById(int id);
    List<Application> findAll();
    List<Application> findByJobSeekerId(int jobSeekerId);
    List<Application> findByJobId(int jobId);
    Application update(Application application);
    void softDelete(int id);
}
