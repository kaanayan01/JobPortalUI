package com.jobportal.service;

import com.jobportal.dto.JobPostPageDTO;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.model.Employer;
import com.jobportal.model.JobPost;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.repository.JobPostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class JobPostService {

	@Autowired
	private JobPostRepository repository;
	
	@Autowired
	private EmployerService employerService;
	

	 private JobPost getJobPostOrThrow(int id) {
	        JobPost job = repository.findById(id);
	        if (job == null) {
	            throw new ResourceNotFoundException("Job post not found with ID: " + id);
	        }
	        return job;
	    }

	    public JobPost create(JobPost jobPost) {
	    	Employer employer = employerService.findById(jobPost.getEmployerId());
	    	if(!ApprovalStatus.APPROVED.equals(employer.getApprovalStatus())) {
	    		throw new UnauthorizedException("You are not allowed to perform this action until approval status is confirmed");
	    	}
	        int id = repository.create(jobPost);   // insert → returns generated ID
	        return getJobPostOrThrow(id);
	    }

	public JobPost findById(int id) {
		JobPost jobPost =  repository.findById(id);
		if(jobPost == null)
			throw new ResourceNotFoundException("Job Post not found");
			
		return jobPost;
	}

	public JobPostPageDTO findAll(int pageNumber, int pageSize, String searchText) {
		searchText = searchText == null ? "" : searchText;
		JobPostPageDTO response = new JobPostPageDTO();
		 response.setTotalRecords(repository.countJobs(searchText));
		 response.setJobPosts( repository.findAll(pageNumber,pageSize ,searchText));
		 return response;
	}

	   public JobPost update(JobPost jobPost) {
	        // ensure exists before updating
	        getJobPostOrThrow(jobPost.getJobId());

	        repository.update(jobPost);

	        // fetch updated data
	        return getJobPostOrThrow(jobPost.getJobId());
	    }

	public int softDelete(int id) {
		return repository.softDelete(id);
	}

	public List<JobPost> findAllActive() {
		return repository.findAllActive();
	}
	
	public List<JobPost> findByEmployerId(int employerId) {
	    return repository.findByEmployerId(employerId);
	}


}
