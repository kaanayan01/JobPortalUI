package com.jobportal.service;

import com.jobportal.model.Report;
import com.jobportal.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    public Report create(Report report) {
        return repository.save(report);
    }

    public Report getById(int id) {
        return repository.findById(id);
    }

    public List<Report> getAll() {
        return repository.findAll();
    }

    public List<Report> getByAdminId(int adminId) {
        return repository.findByAdminId(adminId);
    }

    public Report update(Report report) {
        repository.update(report);
        return report;
    }

    public void delete(int reportId) {
        repository.delete(reportId);
    }
}
