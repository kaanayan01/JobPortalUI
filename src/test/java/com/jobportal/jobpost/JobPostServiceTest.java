package com.jobportal.jobpost;

import com.jobportal.model.JobPost;
import com.jobportal.model.Employer;
import com.jobportal.repository.JobPostRepository;
import com.jobportal.service.EmployerService;
import com.jobportal.service.JobPostService;
import com.jobportal.dto.JobPostPageDTO;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.model.enums.ApprovalStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JobPostServiceTest {

	@Mock
	private JobPostRepository repo;

	@Mock
	private EmployerService employerService;

	@InjectMocks
	private JobPostService service;

	// ========== 1️⃣ CREATE JOB POST ==========
	@Test
	void testCreateJobPostSuccess() {
		JobPost job = new JobPost();
		job.setEmployerId(5);

		Employer emp = new Employer();
		emp.setApprovalStatus(ApprovalStatus.APPROVED);

		when(employerService.findById(5)).thenReturn(emp);
		when(repo.create(job)).thenReturn(10);
		JobPost created=new JobPost();
		created.setJobId(10);
		
		when(repo.findById(10)).thenReturn(created);

		JobPost result = service.create(job);

		assertEquals(10, result.getJobId());
		verify(repo, times(1)).create(job);
	}

	// ========== 2️⃣ CREATE JOB POST (EMPLOYER NOT APPROVED) ==========
	@Test
	void testCreateJobPostEmployerNotApproved() {
		JobPost job = new JobPost();
		job.setEmployerId(7);

		Employer emp = new Employer();
		emp.setApprovalStatus(ApprovalStatus.PENDING);

		when(employerService.findById(7)).thenReturn(emp);

		assertThrows(UnauthorizedException.class, () -> service.create(job));
	}

	// ========== 3️⃣ FIND JOB POST BY ID ==========
	@Test
	void testFindByIdSuccess() {
		JobPost job = new JobPost();
		job.setJobId(3);

		when(repo.findById(3)).thenReturn(job);

		JobPost result = service.findById(3);

		assertEquals(3, result.getJobId());
	}

	// ========== 4️⃣ FIND ALL JOB POSTS ==========
	@Test
	void testFindAll() {
		JobPost j1 = new JobPost();
		j1.setJobId(1);
		
		JobPost j2 = new JobPost();
		j2.setJobId(2);

		when(repo.findAll(anyInt(), anyInt(), anyString())).thenReturn(List.of(j1, j2));

		JobPostPageDTO results = service.findAll(0,1, "abc");

		assertEquals(2, results.getJobPosts().size());
	}

	// ========== 5️⃣ UPDATE JOB POST ==========
	@Test
	void testUpdateJobPost() {
		JobPost job = new JobPost();
		job.setJobId(4);


		when(repo.findById(4)).thenReturn(job); // existence check
//		doNothing().when(repo).update(job); // update
		
		when(repo.update(any(JobPost.class))).thenReturn(1);

		JobPost updated=new JobPost();
		updated.setJobId(4);
		
		
		when(repo.findById(4))
		.thenReturn(job)
		.thenReturn(updated); // fetch updated

		JobPost result = service.update(job);

		assertEquals(4, result.getJobId());
	}
}
