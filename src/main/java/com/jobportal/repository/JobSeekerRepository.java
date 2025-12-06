package com.jobportal.repository;

import com.jobportal.model.JobSeeker;
import com.jobportal.model.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface JobSeekerRepository {

    JobSeeker create(JobSeeker jobSeeker);

    JobSeeker findById(int id);
    
    JobSeeker IsUserId(int id);

    List<JobSeeker> findAll();

    JobSeeker update(JobSeeker jobSeeker);

    int softDelete(int id);
    
    void updateResume(int id,String relativePath);

	void updateSubscriptionType( int userId, LocalDateTime expiry, int paymentId);
}
