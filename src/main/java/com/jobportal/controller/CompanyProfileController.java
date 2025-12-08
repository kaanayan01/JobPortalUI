package com.jobportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.CompanyProfileDTO;
import com.jobportal.dto.GenericResponse;
import com.jobportal.model.CompanyProfile;
import com.jobportal.service.CompanyProfileService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/company-profiles")
public class CompanyProfileController {

    private final CompanyProfileService service;
    

	@Autowired
    private ObjectMapper objectMapper;

    public CompanyProfileController(CompanyProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<CompanyProfile>> create(@RequestBody CompanyProfile profile) {
        CompanyProfile created = service.create(profile);
        return ResponseBuilder.success(created, "Company profile created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<CompanyProfile>> getById(@PathVariable int id) {
        CompanyProfile profile = service.getById(id);
        return ResponseBuilder.success(profile, "Company profile retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<CompanyProfile>>> getAll() {
        List<CompanyProfile> profiles = service.getAll();
        return ResponseBuilder.success(profiles, "All company profiles retrieved successfully");
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<GenericResponse<List<CompanyProfile>>> getByEmployer(@PathVariable int employerId) {
        List<CompanyProfile> profiles = service.getByEmployerId(employerId);
        return ResponseBuilder.success(profiles, "Company profiles for employer retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<CompanyProfile>> update(@PathVariable int id, @RequestBody CompanyProfile profile) {
        profile.setProfileId(id);
        CompanyProfile updated = service.update(profile);
        return ResponseBuilder.success(updated, "Company profile updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseBuilder.success(null, "Company profile deleted successfully");
    }  
    
    


}
