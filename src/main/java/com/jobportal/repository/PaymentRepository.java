package com.jobportal.repository;

import com.jobportal.model.Payment;
import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    Payment findById(int paymentId);
    List<Payment> findAll();
    List<Payment> findByUserId(int userId);
    void update(Payment payment);
    void delete(int paymentId);
}
