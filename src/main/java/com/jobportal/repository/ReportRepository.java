package com.jobportal.repository;

import com.jobportal.model.Report;
import java.util.List;

public interface ReportRepository {
    Report save(Report report);
    Report findById(int reportId);
    List<Report> findAll();
    List<Report> findByAdminId(int adminId);
    void update(Report report);
    void delete(int reportId);
}
