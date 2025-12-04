package com.jobportal.repository;

import com.jobportal.model.CompanyProfile;
import java.util.List;
import java.util.Optional;

public interface CompanyProfileRepository {
    CompanyProfile save(CompanyProfile profile);
    CompanyProfile findById(int profileId);
    List<CompanyProfile> findAll();
    List<CompanyProfile> findByEmployerId(int employerId);
    CompanyProfile update(CompanyProfile profile);
    void softDelete(int profileId);

}
