package com.jobportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import com.jobportal.dto.GenericResponse;
import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.LoginResponse;
import com.jobportal.dto.UserDTO;
import com.jobportal.model.User;
import com.jobportal.service.FileStorageService;
import com.jobportal.service.UserService;
import com.jobportal.utils.ResponseBuilder;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
    private UserService service;
	
	@Autowired
    private ObjectMapper objectMapper;

    // CREATE USER
    @PostMapping
    public ResponseEntity<GenericResponse<User>> create(@Valid @RequestBody User user) {
    	System.out.println("Request");
        User createdUser = service.create(user);
        return ResponseBuilder.created(createdUser, "User created successfully");
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<User>> getById(@PathVariable int id) {
        User user = service.findById(id);
        return ResponseBuilder.success(user, "User fetched successfully");
    }

    // GET ALL USERS
    @GetMapping
    public ResponseEntity<GenericResponse<List<User>>> getAll() {
        List<User> users = service.findAll();
        return ResponseBuilder.success(users, "Users fetched successfully");
    }

    // UPDATE USER
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<User>> update(@PathVariable int id, @Valid @RequestBody User user) {
        user.setUserId(id);
        User updatedUser = service.update(user);
        return ResponseBuilder.success(updatedUser, "User updated successfully");
    }

    // SOFT DELETE USER
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseBuilder.success(null, "User soft deleted successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<GenericResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
       LoginResponse loginResponse = service.login(request.getEmail(), request.getPassword());
       
        return ResponseBuilder.success(loginResponse, "Login successful");
    }
    
    
    @PostMapping(value = "/{id}/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<UserDTO>> uploadProfilePhoto(
            @PathVariable int id,
            @RequestParam("file") MultipartFile file){

        User updated = service.updateProfileImage(id, file);

        // convert entity to DTO using ObjectMapper
        UserDTO dto = objectMapper.convertValue(updated, UserDTO.class);

        return ResponseBuilder.success(dto, "Profile photo updated");
    }

}
