package com.jobportal.service;

import com.jobportal.model.Admin;
import com.jobportal.model.CompanyProfile;
import com.jobportal.model.Employer;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    public Admin create(Admin admin) {
        return repository.save(admin);
    }

    public Admin findById(int adminId) {
        return repository.findById(adminId);
    }

    public List<Admin> findAll() {
        return repository.findAll();
    }

    public List<Employer> listPendingEmployers() {
        return repository.findAllPendingEmployers();
    }

    public void approveEmployer(int employerId) {
        repository.updateEmployerStatus(employerId, ApprovalStatus.APPROVED);
    }

    public void rejectEmployer(int employerId) {
        repository.updateEmployerStatus(employerId, ApprovalStatus.REJECTED);
    }
    
    public CompanyProfile viewCompanyProfile(int employerId) {
        return repository.findCompanyProfileByEmployer(employerId);
    }
    
    

	public Employer approveOrRejectEmployer(int id, ApprovalStatus status) {
		int updated = repository.updateEmployerStatus(id, status);
        if (updated == 0) throw new IllegalArgumentException("Employer not found with id: " + id);

        Employer e = new Employer();
        e.setEmployerId(id);
        e.setApprovalStatus(status);
        return e;
	}
}
