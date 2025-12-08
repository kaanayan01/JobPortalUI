package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.Payment;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.service.PaymentService;
import com.jobportal.utils.ResponseBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
	public ResponseEntity<GenericResponse<Payment>> create(@RequestBody Payment payment) {
		Payment created = service.create(payment);
		return ResponseBuilder.success(created, "Payment created successfully");
	}
    
    @PostMapping("/{id}")
	public ResponseEntity<GenericResponse<PaymentStatus>> processPayment(@PathVariable int id) {
    	PaymentStatus status =  service.processPayment(id);
		return ResponseBuilder.success(status, "Payment " + status);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GenericResponse<Payment>> getById(@PathVariable int id) {
		Payment payment = service.getById(id);
		return ResponseBuilder.success(payment, "Payment retrieved successfully");
	}

	@GetMapping
	public ResponseEntity<GenericResponse<List<Payment>>> getAll() {
		List<Payment> payments = service.getAll();
		return ResponseBuilder.success(payments, "All payments retrieved successfully");
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<GenericResponse<List<Payment>>> getByUser(@PathVariable int userId) {
		List<Payment> payments = service.getByUserId(userId);
		return ResponseBuilder.success(payments, "Payments for user retrieved successfully");
	}

	@PutMapping("/{id}")
	public ResponseEntity<GenericResponse<Payment>> update(@PathVariable int id, @RequestBody Payment payment) {
		payment.setPaymentId(id);
		service.update(payment);
		return ResponseBuilder.success(null, "Payment updated successfully");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<GenericResponse<String>> delete(@PathVariable int id) {
		service.delete(id);
		return ResponseBuilder.success(null, "Payment deleted successfully");
	}
}
