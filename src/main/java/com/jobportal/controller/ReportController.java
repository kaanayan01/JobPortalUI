package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.Report;
import com.jobportal.service.ReportService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<GenericResponse<Report>> create(@RequestBody Report report) {
        Report created = service.create(report);
        return ResponseBuilder.success(created, "Report created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Report>> getById(@PathVariable int id) {
        Report r = service.getById(id);
        return ResponseBuilder.success(r, "Report retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<Report>>> getAll() {
        List<Report> reports = service.getAll();
        return ResponseBuilder.success(reports, "All reports retrieved successfully");
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<GenericResponse<List<Report>>> getByAdmin(@PathVariable int adminId) {
        List<Report> reports = service.getByAdminId(adminId);
        return ResponseBuilder.success(reports, "Reports for admin retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<Report>> update(@PathVariable int id, @RequestBody Report report) {
        report.setReportId(id);
        Report updated = service.update(report);
        return ResponseBuilder.success(updated, "Report updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseBuilder.success(null, "Report deleted successfully");
    }
    
}
