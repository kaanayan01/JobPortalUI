package com.jobportal.model;

import com.jobportal.model.enums.JobType;
import com.jobportal.model.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class JobPost {

	private int jobId;

	@NotNull(message = "Employer ID is required")
	private Integer employerId;

	@NotNull(message = "Title is required")
	@Size(max = 150, message = "Title max length is 150")
	private String title;

	private String description;

	private List<String> requirements;

	@Size(max = 150, message = "Job location max length is 150")
	private String jobLocation;

	@Size(max = 100, message = "Salary max length is 100")
	private String salary;

	@NotNull(message = "Job type is required")
	private JobType jobType;

	private JobStatus status = JobStatus.ACTIVE;

	private LocalDateTime postedDate = LocalDateTime.now();

	// ======= GETTERS & SETTERS =======
	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public Integer getEmployerId() {
		return employerId;
	}

	public void setEmployerId(Integer employerId) {
		this.employerId = employerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public List<String> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

	public String getJobLocation() {
		return jobLocation;
	}

	public void setJobLocation(String jobLocation) {
		this.jobLocation = jobLocation;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public LocalDateTime getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(LocalDateTime postedDate) {
		this.postedDate = postedDate;
	}
}
