package com.jobportal.service;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Application;
import com.jobportal.model.JobPost;
import com.jobportal.model.JobSeeker;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.model.enums.ApplicationStatus;
import com.jobportal.model.enums.JobStatus;
import com.jobportal.model.enums.SubscriptionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import javax.naming.LimitExceededException;

@Service
public class ApplicationService {

	@Autowired
    private ApplicationRepository repository;
	@Autowired
	private JobSeekerService jobSeekerService;
	@Autowired
	private JobPostService jobPostService;

    public Application create(Application a) throws LimitExceededException {
    	JobSeeker jobSeeker = jobSeekerService.findById(a.getJobSeekerId());
    	if(jobSeeker == null) {
    		throw new ResourceNotFoundException("Job Seeker does not exist");
    	}
    	JobPost jobPost = jobPostService.findById(a.getJobId());
    	
    	if(jobPost == null || jobPost.getStatus() != JobStatus.ACTIVE) {
    		throw new ResourceNotFoundException("Job does not exist or not active");
    	}
    	if(jobSeeker.getSubscriptionType() == SubscriptionType.FREE) {
    	int applicationsToday = repository.findByJobIdAndJobSeekerIdOnDate(a.getJobId(), a.getJobSeekerId(), LocalDateTime.now());
    	 	if(applicationsToday >= 5) {
    	 		throw new LimitExceededException("Limit exhausted for the day. Kindly upgrade to apply");
    	 	}
    	}
    	
    	
    
    	if(a.getStatus() == null) a.setStatus(ApplicationStatus.APPLIED);
        return repository.save(a);
    }

    public Application findById(int id) {
        return repository.findById(id);
    }

    public List<Application> findAll() {
        return repository.findAll();
    }

    public List<Application> findByJobSeekerId(int jobSeekerId) {
        return repository.findByJobSeekerId(jobSeekerId);
    }

    public List<ApplicationDTO> findByJobId(int jobId) {
        return repository.findByJobId(jobId);
    }

    public Application update(Application a) {
        return repository.update(a);
    }

    public void softDelete(int id) {
        repository.softDelete(id);
    }
}
