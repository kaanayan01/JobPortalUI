package com.jobportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.GenericResponse;
import com.jobportal.dto.JobSeekerDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.User;
import com.jobportal.service.JobSeekerService;
import com.jobportal.utils.ResponseBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/jobseekers")
public class JobSeekerController {

    private final JobSeekerService service;
    

	@Autowired
    private ObjectMapper objectMapper;

    public JobSeekerController(JobSeekerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<JobSeeker>> create(@Valid @RequestBody JobSeeker jobSeeker) {
        JobSeeker created = service.create(jobSeeker);
        return ResponseBuilder.success(created, "Job seeker created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<JobSeeker>> getById(@PathVariable int id) {
        JobSeeker js = service.findById(id);
        return ResponseBuilder.success(js, "Job seeker retrieved successfully");
    }


	@GetMapping("/checkuser/{id}")
	public ResponseEntity<GenericResponse<JobSeeker>> isUserId(@PathVariable int id) {
	    System.out.println("Received ID: " + id); // ✅ Print for testing
	    
	    JobSeeker js = service.IsUserId(id);
	    System.out.println("JobSeeker object: " + js); // ✅ Print object
	    
	    return ResponseBuilder.success(js, "Job seeker retrieved successfully");
	}

    
    @GetMapping
    public ResponseEntity<GenericResponse<List<JobSeeker>>> getAll() {
        List<JobSeeker> jobSeekers = service.findAll();
        return ResponseBuilder.success(jobSeekers, "All job seekers retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<JobSeeker>> update(@PathVariable int id, @Valid @RequestBody JobSeeker jobSeeker) {
        jobSeeker.setJobSeekerId(id);
        JobSeeker updated = service.update(jobSeeker);
        return ResponseBuilder.success(updated, "Job seeker updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.softDelete(id);
        return ResponseBuilder.success(null, "Job seeker soft deleted successfully");
    }
    
    @PostMapping(value = "/{id}/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<JobSeekerDTO>> uploadResume(
            @PathVariable int id,
            @RequestParam("file") MultipartFile file){

        JobSeeker updated = service.updateResume(id, file);

        // convert entity to DTO using ObjectMapper
        JobSeekerDTO dto = objectMapper.convertValue(updated, JobSeekerDTO.class);

        return ResponseBuilder.success(dto, "Profile photo updated");
    }

    
}
