package com.jobportal.service;

import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Application;
import com.jobportal.model.JobPost;
import com.jobportal.model.JobSeeker;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.model.enums.ApplicationStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationService {

	@Autowired
    private ApplicationRepository repository;
	@Autowired
	private JobSeekerService jobSeekerService;
	@Autowired
	private JobPostService jobPostService;

    public Application create(Application a) {
    	JobSeeker jobSeeker = jobSeekerService.findById(a.getJobSeekerId());
    	if(jobSeeker == null) {
    		throw new ResourceNotFoundException("Job Seeker does not exist");
    	}
    	JobPost jobPost= jobPostService.findById(a.getJobId());
    	
    	if(jobPost == null) {
    		throw new ResourceNotFoundException("Job does not exist");
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

    public List<Application> findByJobId(int jobId) {
        return repository.findByJobId(jobId);
    }

    public Application update(Application a) {
        return repository.update(a);
    }

    public void softDelete(int id) {
        repository.softDelete(id);
    }
}
