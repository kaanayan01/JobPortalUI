package com.jobportal.service;

import com.jobportal.exception.PaymentFailedException;
import com.jobportal.model.Payment;
import com.jobportal.model.Subscription;
import com.jobportal.model.User;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.model.enums.UserType;
import com.jobportal.repository.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class PaymentService {

    private final EmployerService employerService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository repository;
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private JobSeekerService jobSeekerService;
    
    
    

    public PaymentService(PaymentRepository repository, EmployerService employerService) {
        this.repository = repository;
        this.employerService = employerService;
    }

    public Payment create(Payment payment) {
        return repository.save(payment);
    }

    public Payment getById(int id) {
        return repository.findById(id);
    }

    public List<Payment> getAll() {
        return repository.findAll();
    }

    public List<Payment> getByUserId(int userId) {
        return repository.findByUserId(userId);
    }

    public void update(Payment payment) {
    
    	if(payment.getStatus() == PaymentStatus.SUCCESS) {
	    	int userId = payment.getUserId();
	    	
	    	
	    	User user = userService.findById(payment.getUserId());
	    	Subscription subscription = subscriptionService.getById(payment.getSubscriptionId());
	    	LocalDateTime expiry = payment.getPaymentDate().plusDays( subscription.getDuration());
	    	if(user.getUserType() == UserType.EMPLOYER) {
	    		employerService.updateSubscriptionType(userId,expiry, payment.getPaymentId());
	    	}
	    	else if(user.getUserType() == UserType.JOB_SEEKER) {
	    		jobSeekerService.updateSubscriptionType(userId, expiry, payment.getPaymentId());
	    	}
    	}
        repository.update(payment);
    	
        
    }

    public void delete(int paymentId) {
        repository.delete(paymentId);
    }

	public PaymentStatus processPayment(int id) {
	
		 try {
	            // Simulate payment gateway delay
	            long delay = ThreadLocalRandom.current().nextLong(1000, 3000);
	            Thread.sleep(delay);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            return PaymentStatus.FAILED;
	        }

	        // Random outcome
	        boolean success = ThreadLocalRandom.current().nextBoolean();
	        if(success) {
	        	return PaymentStatus.SUCCESS;
	        }
	        throw new PaymentFailedException("Payment failed");

	   
	    }
		
}
