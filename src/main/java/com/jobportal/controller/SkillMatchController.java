package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.SkillMatch;
import com.jobportal.service.SkillMatchService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-matches")
public class SkillMatchController {

    private final SkillMatchService service;

    public SkillMatchController(SkillMatchService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<SkillMatch>> create(@RequestBody SkillMatch skillMatch) {
        SkillMatch created = service.create(skillMatch);
        return ResponseBuilder.success(created, "Skill match created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<SkillMatch>> getById(@PathVariable int id) {
        SkillMatch match = service.getById(id);
        return ResponseBuilder.success(match, "Skill match retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<SkillMatch>>> getAll() {
        List<SkillMatch> list = service.getAll();
        return ResponseBuilder.success(list, "All skill matches retrieved successfully");
    }

    @GetMapping("/job-seeker/{jobSeekerId}")
    public ResponseEntity<GenericResponse<List<SkillMatch>>> getByJobSeeker(@PathVariable int jobSeekerId) {
        List<SkillMatch> list = service.getByJobSeekerId(jobSeekerId);
        return ResponseBuilder.success(list, "Skill matches for job seeker retrieved successfully");
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<GenericResponse<List<SkillMatch>>> getByJob(@PathVariable int jobId) {
        List<SkillMatch> list = service.getByJobId(jobId);
        return ResponseBuilder.success(list, "Skill matches for job retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<SkillMatch>> update(@PathVariable int id, @RequestBody SkillMatch skillMatch) {
        skillMatch.setMatchId(id);
        SkillMatch updated = service.update(skillMatch);
        return ResponseBuilder.success(updated, "Skill match updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseBuilder.success(null, "Skill match deleted successfully");
    }   
}
