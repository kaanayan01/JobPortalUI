package com.jobportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jobportal.model.Admin;
import com.jobportal.model.Application;
import com.jobportal.model.Employer;
import com.jobportal.model.JobPost;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.Payment;
import com.jobportal.model.Report;
import com.jobportal.model.Subscription;
import com.jobportal.model.User;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.model.enums.JobStatus;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.model.enums.UserStatus;
import com.jobportal.repository.AdminRepository;

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
    
    public Employer approveOrRejectEmployer(int id, ApprovalStatus status) {
        int updated = repository.updateEmployerStatus(id, status);
        if (updated == 0) throw new IllegalArgumentException("Employer not found with id: " + id);

        Employer e = new Employer();
        e.setEmployerId(id);
        e.setApprovalStatus(status);
        return e;
    }
    
    // ==========================================================
    // Job Posting Monitoring Methods
    // ==========================================================
    public List<JobPost> getAllJobPosts() {
        return repository.findAllJobPosts();
    }
    
    public JobPost getJobPostById(int jobId) {
        return repository.findJobPostById(jobId);
    }
    
    public JobPost updateJobPostStatus(int jobId, JobStatus status) {
        int updated = repository.updateJobPostStatus(jobId, status);
        if (updated == 0) throw new IllegalArgumentException("Job post not found with id: " + jobId);
        JobPost jobPost = repository.findJobPostById(jobId);
        return jobPost;
    }
    
    public List<JobPost> getJobPostsByStatus(JobStatus status) {
        return repository.findJobPostsByStatus(status);
    }
    
    public List<JobPost> getJobPostsByEmployerId(int employerId) {
        return repository.findJobPostsByEmployerId(employerId);
    }
    
    // ==========================================================
    // Premium Plans & Subscription Management Methods
    // ==========================================================
    public List<Subscription> getAllSubscriptions() {
        return repository.findAllSubscriptions();
    }
    
    public Subscription updateSubscription(int subscriptionId, Subscription subscription) {
        subscription.setSubscriptionId(subscriptionId);
        int updated = repository.updateSubscription(subscriptionId, subscription);
        if (updated == 0) throw new IllegalArgumentException("Subscription not found with id: " + subscriptionId);
        // Return the updated subscription by finding it from the list
        List<Subscription> subscriptions = repository.findAllSubscriptions();
        return subscriptions.stream()
                .filter(s -> s.getSubscriptionId() == subscriptionId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found after update"));
    }
    
    public List<Employer> getPremiumEmployers() {
        return repository.findPremiumEmployers();
    }
    
    public List<JobSeeker> getPremiumJobSeekers() {
        return repository.findPremiumJobSeekers();
    }
    
    public List<Payment> getAllPayments() {
        return repository.findAllPayments();
    }
    
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return repository.findPaymentsByStatus(status);
    }
    
    public Map<String, Object> getPaymentStatistics() {
        return repository.getPaymentStatistics();
    }
    
    // ==========================================================
    // Reports & Analytics Methods
    // ==========================================================
    public Report generateReport(Report report) {
        return repository.saveReport(report);
    }
    
    public List<Report> getAllReports() {
        return repository.findAllReports();
    }
    
    public Report getReportById(int reportId) {
        return repository.findReportById(reportId);
    }
    
    public Map<String, Object> getDashboardAnalytics() {
        return repository.getDashboardAnalytics();
    }
    
    public Map<String, Object> getJobAnalytics() {
        return repository.getJobAnalytics();
    }
    
    public Map<String, Object> getApplicationAnalytics() {
        return repository.getApplicationAnalytics();
    }
    
    public Map<String, Object> getUserAnalytics() {
        return repository.getUserAnalytics();
    }
    
    // ==========================================================
    // User Management & Activity Monitoring Methods
    // ==========================================================
    public List<User> getAllUsers() {
        return repository.findAllUsers();
    }
    
    public User getUserById(int userId) {
        return repository.findUserById(userId);
    }
    
    public User updateUserStatus(int userId, UserStatus status) {
        int updated = repository.updateUserStatus(userId, status);
        if (updated == 0) throw new IllegalArgumentException("User not found with id: " + userId);
        return repository.findUserById(userId);
    }
    
    public List<Application> getAllApplications() {
        return repository.findAllApplications();
    }
    
    public Map<String, Object> getApplicationStatistics() {
        return repository.getApplicationStatistics();
    }
    
    // ==========================================================
    // Additional Management Methods
    // ==========================================================
    public List<Employer> getEmployersByApprovalStatus(ApprovalStatus status) {
        return repository.findEmployersByApprovalStatus(status);
    }
    
    public List<JobSeeker> getAllJobSeekers() {
        return repository.findAllJobSeekers();
    }
    
    // Enhanced methods returning Maps with joined data for admin views
    public List<Map<String, Object>> getEmployersWithDetails(ApprovalStatus status) {
        return repository.findEmployersWithDetails(status);
    }
    
    public List<Map<String, Object>> getAllEmployersWithDetails() {
        return repository.findAllEmployersWithDetails();
    }
    
    public List<Map<String, Object>> getPendingEmployersWithDetails() {
        return repository.findPendingEmployersWithDetails();
    }
    
    public List<Map<String, Object>> getPremiumEmployersWithDetails() {
        return repository.findPremiumEmployersWithDetails();
    }
    
    public List<Map<String, Object>> getJobSeekersWithDetails() {
        return repository.findJobSeekersWithDetails();
    }
    
    public List<Map<String, Object>> getPremiumJobSeekersWithDetails() {
        return repository.findPremiumJobSeekersWithDetails();
    }
    
    public List<Map<String, Object>> getApplicationsWithDetails() {
        return repository.findApplicationsWithDetails();
    }
    
    public List<Map<String, Object>> getPaymentsWithDetails() {
        return repository.findPaymentsWithDetails();
    }
    
    public List<Map<String, Object>> getPaymentsByStatusWithDetails(PaymentStatus status) {
        return repository.findPaymentsByStatusWithDetails(status);
    }
    
    public List<Map<String, Object>> getAllReportsWithDetails() {
        return repository.findAllReportsWithDetails();
    }
}
