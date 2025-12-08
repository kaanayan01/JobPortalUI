 package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.Employer;
import com.jobportal.model.JobSeeker;
import com.jobportal.service.EmployerService;
import com.jobportal.utils.ResponseBuilder;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

	private final EmployerService service;

	public EmployerController(EmployerService service) {
		this.service = service;
	}


	@PostMapping
    public ResponseEntity<GenericResponse<Employer>> create(@Valid @RequestBody Employer employer) {
        Employer created = service.create(employer);
        return ResponseBuilder.success(created, "Employer created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Employer>> getById(@PathVariable int id) {
        Employer employer = service.findById(id);
        return ResponseBuilder.success(employer, "Employer retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<Employer>>> getAll() {
        List<Employer> employers = service.findAll();
        return ResponseBuilder.success(employers, "All employers retrieved successfully");
    }
    
    
	@GetMapping("/checkuser/{id}")
	public ResponseEntity<GenericResponse<Employer>> isUserId(@PathVariable int id) {
	    System.out.println("Received ID: " + id); // ✅ Print for testing
	    
	    Employer employer = service.isUserId(id);
        return ResponseBuilder.success(employer, "Employer retrieved successfully");
	}


    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<Employer>> update(@PathVariable int id, @Valid @RequestBody Employer employer) {
        employer.setEmployerId(id);
        Employer updated = service.update(employer);
        return ResponseBuilder.success(updated, "Employer updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.softDelete(id);
        return ResponseBuilder.success(null, "Employer soft deleted successfully");
    }

    @GetMapping("/approved")
    public ResponseEntity<GenericResponse<List<Employer>>> getAllApproved() {
        List<Employer> approved = service.findAllApproved();
        return ResponseBuilder.success(approved, "Approved employers retrieved successfully");
    }

    @GetMapping("/pending")
    public ResponseEntity<GenericResponse<List<Employer>>> getAllPending() {
        List<Employer> pending = service.findAllPending();
        return ResponseBuilder.success(pending, "Pending employers retrieved successfully");
    }
}
