package com.jobportal.repository;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.model.Application;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationRepository {
    Application save(Application application);
    Application findById(int id);
    List<Application> findAll();
    List<Application> findByJobSeekerId(int jobSeekerId);
    List<ApplicationDTO> findByJobId(int jobId);
    Application update(Application application);
    void softDelete(int id);
	int findByJobIdAndJobSeekerIdOnDate(int jobId, int jobSeekerId, LocalDateTime now);
}
