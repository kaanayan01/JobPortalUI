package com.jobportal.dto;

import java.util.List;

import com.jobportal.model.JobPost;

public class JobPostPageDTO {
	
	private int totalRecords;
	
	private List<JobPost> jobPosts;

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List<JobPost> getJobPosts() {
		return jobPosts;
	}

	public void setJobPosts(List<JobPost> jobPosts) {
		this.jobPosts = jobPosts;
	}

}
