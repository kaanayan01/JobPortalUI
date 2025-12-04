package com.jobportal.repository;

import com.jobportal.model.Admin;
import com.jobportal.model.CompanyProfile;
import com.jobportal.model.Employer;
import com.jobportal.model.enums.ApprovalStatus;
import java.util.List;

public interface AdminRepository {
    Admin save(Admin admin);
    Admin findById(int adminId);
    List<Admin> findAll();

    List<Employer> findAllPendingEmployers();
    
    CompanyProfile findCompanyProfileByEmployer(int employerId);
    
    int updateEmployerStatus(int employerId, ApprovalStatus status);
}
