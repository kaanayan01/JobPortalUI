package com.jobportal.jobseeker;

import com.jobportal.model.JobSeeker;
import com.jobportal.model.User;
import com.jobportal.model.enums.UserType;
import com.jobportal.repository.JobSeekerRepository;
import com.jobportal.service.FileStorageService;
import com.jobportal.service.JobSeekerService;
import com.jobportal.service.UserService;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerServiceTest {

	@Mock
	private JobSeekerRepository repo;

	@Mock
	private UserService userService;

	@Mock
	private FileStorageService fileStorageService;

	@InjectMocks
	private JobSeekerService service;

// ========== 1️⃣ CREATE JOB SEEKER (SUCCESS) ==========
	@Test
	void testCreateJobSeekerSuccess() {

		JobSeeker js = new JobSeeker();
		js.setUserId(10);

		User user = new User();
		user.setUserType(UserType.JOB_SEEKER);

		when(userService.findById(10)).thenReturn(user);
		when(repo.create(js)).thenReturn(js);

		JobSeeker result = service.create(js);

		assertEquals(10, result.getUserId());
		verify(repo, times(1)).create(js);
	}

// ========== 2️⃣ CREATE JOB SEEKER (WRONG USER TYPE) ==========
	@Test
	void testCreateJobSeekerWrongUserType() {

		JobSeeker js = new JobSeeker();
		js.setUserId(20);

		User user = new User();
		user.setUserType(UserType.EMPLOYER); // 

		when(userService.findById(20)).thenReturn(user);

		assertThrows(BadRequestException.class, () -> service.create(js));
		
		verify(repo,never()).create(any());
	}

// ========== 3️⃣ FIND BY ID NOT FOUND ==========
	@Test
	void testFindByIdNotFound() {

		when(repo.findById(5)).thenReturn(null);

		assertThrows(ResourceNotFoundException.class, () -> service.findById(5));
	}

// ========== 4️⃣ UPDATE JOB SEEKER ==========
	@Test
	void testUpdateJobSeeker() {

		JobSeeker js = new JobSeeker();
		js.setJobSeekerId(3);

		JobSeeker updated = new JobSeeker();
		updated.setJobSeekerId(3);

		when(repo.update(js)).thenReturn(updated);

		JobSeeker result = service.update(js);

		assertEquals(3, result.getJobSeekerId());
		verify(repo, times(1)).update(js);
	}


}