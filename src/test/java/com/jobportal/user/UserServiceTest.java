package com.jobportal.user;

import com.jobportal.model.User;

import com.jobportal.model.enums.UserStatus;
import com.jobportal.repository.UserRepository;
import com.jobportal.dto.LoginResponse;
import com.jobportal.dto.UserDTO;
import com.jobportal.service.FileStorageService;
//import com.jobportal.service.JwtService;
import com.jobportal.service.UserService;
import com.jobportal.utils.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository repo;

	@Mock
	private FileStorageService fileStorageService;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private UserService service;

// ========== 1️⃣ CREATE USER TEST ==========
	@Test
	void testCreateUser() {
		User input = new User();
		input.setPassword("123456");

		User created = new User();
		created.setUserId(1);
		created.setPassword("hashedPassword");

		when(repo.create(any(User.class))).thenReturn(created);
		when(repo.findById(1)).thenReturn(Optional.of(created));

		User result = service.create(input);

		assertEquals(1, result.getUserId());
		verify(repo, times(1)).create(any());
	}

// ========== 2️⃣ FIND USER BY ID ==========
	@Test
	void testFindById() {
		User u = new User();
		u.setUserId(5);

		when(repo.findById(5)).thenReturn(Optional.of(u));

		User result = service.findById(5);

		assertEquals(5, result.getUserId());
	}

// ========== 3️⃣ DELETE USER ==========
	@Test
	void testDeleteUser() {
		when(repo.delete(10)).thenReturn(1);

		service.delete(10);

		verify(repo, times(1)).delete(10);
	}

// ========== 4️⃣ LOGIN SUCCESS ==========
//	@Test
//	void testLoginSuccess() {
//	User u = new User();
//	u.setUserId(1);
//	u.setEmail("test@gmail.com");
//	u.setPassword("hashedPassword");
//	u.setStatus(UserStatus.PENDING);
//
//	// MOCK: find user by email
//	when(repo.findByEmail("test@gmail.com")).thenReturn(u);
//
//	// MOCK: After login, PENDING → ACTIVE (so repo.update() is called)
//	when(repo.update(any(User.class))).thenReturn(u);
//
//	// MOCK: Password check always returns true
//	mockStatic(PasswordUtil.class).when(
//	() -> PasswordUtil.checkPassword("1234", "hashedPassword")
//	).thenReturn(true);
//
//	// MOCK: JWT generation
////	when(JwtService.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");
//
//	// MOCK: Convert user to DTO
//	when(objectMapper.convertValue(any(), eq(UserDTO.class))).thenReturn(new UserDTO());
//
//	LoginResponse response = service.login("test@gmail.com", "1234");
//
//	assertNotNull(response);
//	assertEquals("fake-jwt-token", response.getToken());
//	}


//// ========== 5️⃣ UPDATE PROFILE IMAGE ==========
//	@SuppressWarnings("unchecked")
//	@Test
//	void testUpdateProfileImage() {
//	MultipartFile file = mock(MultipartFile.class);
//
//	when(file.isEmpty()).thenReturn(false);
//
//	User existing = new User();
//	existing.setUserId(7);
//
//	// First fetch for validation
//	when(repo.findById(7)).thenReturn(Optional.of(existing));
//
//	// Mock file save
//	when(fileStorageService.store(file, "users", "user_7"))
//	.thenReturn("users/user_7.png");
//
//	// Mock DB update
//	doNothing().when(repo).updateProfileImage(7, "users/user_7.png");
//
//	// Second findById() after update
//	User updated = new User();
//	updated.setUserId(7);
//	updated.setProfileImage("users/user_7.png");
//
//	when(repo.findById(7)).thenReturn(Optional.of(existing), Optional.of(updated));
//
//	User result = service.updateProfileImage(7, file);
//
//	assertEquals("users/user_7.png", result.getProfileImage());
//	}




}