package com.jobportal.service;

import com.jobportal.model.Payment;
import com.jobportal.model.Subscription;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.repository.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class PaymentService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository repository;
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
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

	        return success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
	    }
		
}
