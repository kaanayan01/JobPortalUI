package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.dto.JobPostPageDTO;
import com.jobportal.model.JobPost;
import com.jobportal.service.JobPostService;
import com.jobportal.utils.ResponseBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.naming.LimitExceededException;

@RestController
@RequestMapping("/api/jobs")
public class JobPostController {

    private final JobPostService service;

    public JobPostController(JobPostService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<GenericResponse<JobPost>> create(@Valid @RequestBody JobPost jobPost) throws LimitExceededException {
        JobPost created = service.create(jobPost);
        return ResponseBuilder.success(created, "Job post created successfully");
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<JobPost>> getById(@PathVariable int id) {
        JobPost jobPost = service.findById(id);
        return ResponseBuilder.success(jobPost, "Job post retrieved successfully");
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<GenericResponse<JobPostPageDTO>> getAll(@Positive(message = "Page size should be real number") @RequestParam(defaultValue="20" )int pageSize,
    		@PositiveOrZero(message = "Page size should be whole number") @RequestParam(defaultValue="0" )int pageNumber,
    		@RequestParam(required= false)  String searchText ) {

    	JobPostPageDTO list = service.findAll(pageSize,pageNumber,searchText);
        return ResponseBuilder.success(list, "All job posts retrieved");
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<JobPost>> update(@PathVariable int id, @Valid @RequestBody JobPost jobPost) {
        jobPost.setJobId(id);
        JobPost updated = service.update(jobPost);
        return ResponseBuilder.success(updated, "Job post updated successfully");
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> delete(@PathVariable int id) {
        service.softDelete(id);
        return ResponseBuilder.success(null, "Job post soft deleted successfully");
    }

    // GET ALL ACTIVE
    @GetMapping("/active")
    public ResponseEntity<GenericResponse<List<JobPost>>> getAllActive() {
        List<JobPost> list = service.findAllActive();
        return ResponseBuilder.success(list, "Active job posts retrieved");
    }
    
    @GetMapping("/employer/{employerId}")
    public ResponseEntity<GenericResponse<List<JobPost>>> getByEmployer(@PathVariable int employerId) {
        List<JobPost> jobs = service.findByEmployerId(employerId);
        return ResponseBuilder.success(jobs, "Job posts for employer fetched successfully");
    }
    
   
}
