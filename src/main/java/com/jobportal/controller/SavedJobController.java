package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.SavedJob;
import com.jobportal.service.SavedJobService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobController {

    private final SavedJobService service;

    public SavedJobController(SavedJobService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<GenericResponse<SavedJob>> saveJob(@RequestBody SavedJob savedJob) {
        SavedJob saved = service.save(savedJob);
        return ResponseBuilder.success(saved, "Job saved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<SavedJob>> getById(@PathVariable int id) {
        SavedJob saved = service.findById(id);
        return ResponseBuilder.success(saved, "Saved job retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<SavedJob>>> getAll() {
        List<SavedJob> list = service.findAll();
        return ResponseBuilder.success(list, "All saved jobs retrieved successfully");
    }

    @GetMapping("/jobseeker/{jobSeekerId}")
    public ResponseEntity<GenericResponse<List<SavedJob>>> getByJobSeeker(@PathVariable int jobSeekerId) {
        List<SavedJob> list = service.findByJobSeekerId(jobSeekerId);
        return ResponseBuilder.success(list, "Saved jobs for job seeker retrieved successfully");
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<GenericResponse<List<SavedJob>>> getByJob(@PathVariable int jobId) {
        List<SavedJob> list = service.findByJobId(jobId);
        return ResponseBuilder.success(list, "Saved jobs for job retrieved successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseBuilder.success(null, "Saved job deleted successfully");
    }
   
}
