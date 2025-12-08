package com.jobportal.service;

import com.jobportal.model.CompanyProfile;
import com.jobportal.repository.CompanyProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyProfileService {

    private final CompanyProfileRepository repository;

    public CompanyProfileService(CompanyProfileRepository repository) {
        this.repository = repository;
    }

    public CompanyProfile create(CompanyProfile profile) {
        return repository.save(profile);
    }

    public CompanyProfile getById(int id) {
        return repository.findById(id);
    }

    public List<CompanyProfile> getAll() {
        return repository.findAll();
    }

    public List<CompanyProfile> getByEmployerId(int employerId) {
        return repository.findByEmployerId(employerId);
    }

    public CompanyProfile update(CompanyProfile profile) {
    	return repository.update(profile);
	
    }

    public void delete(int profileId) {
        repository.softDelete(profileId);
    }
}
