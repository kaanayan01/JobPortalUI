package com.jobportal.service;

import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Employer;
import com.jobportal.model.User;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.model.enums.UserType;
import com.jobportal.repository.EmployerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployerService {
	
	@Autowired
	private EmployerRepository repository;
	
	@Autowired
	private UserService userService;

	
	public Employer create(Employer employer) {
		User user = userService.findById(employer.getUserId());
		if(!UserType.EMPLOYER.equals(user.getUserType())) {
			throw new BadRequestException("Required role is missing");
		}
		return repository.create(employer);
	}

	public Employer findById(int id) {
		Employer employer = repository.findById(id);
		if(employer == null) {
			throw new ResourceNotFoundException("Employer does not exist");
		}
		return employer;
	}

	public Employer isUserId(int id) {
		Employer employer = repository.isUserId(id);
		if(employer == null) {
			throw new ResourceNotFoundException("Employer does not exist");
		}
		return employer;
	}
	public List<Employer> findAll() {
		return repository.findAll();
	}

	public Employer update(Employer employer) {
		return repository.update(employer);
	}

	public int softDelete(int id) {
		return repository.softDelete(id);
	}

	public List<Employer> findAllApproved() {
		return repository.findAllApproved();
	}

	public List<Employer> findAllPending() {
		return repository.findAllPending();
	}

	public void updateSubscriptionType( int userId, LocalDateTime expiry, int paymentId) {
		repository.updateSubscriptionType(userId,expiry, paymentId );
		
	}
}
