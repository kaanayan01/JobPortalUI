package com.jobportal.repository;

import com.jobportal.model.Employer;
import java.util.List;

public interface EmployerRepository {

    Employer create(Employer employer);

    Employer findById(int id);

    List<Employer> findAll();

    Employer update(Employer employer);

    int softDelete(int id);

    List<Employer> findAllApproved();

    List<Employer> findAllPending();

	Employer isUserId(int id);
}
