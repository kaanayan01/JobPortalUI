package com.jobportal.repository;

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
import java.util.List;
import java.util.Map;

public interface AdminRepository {
    Admin save(Admin admin);
    Admin findById(int adminId);
    List<Admin> findAll();

    List<Employer> findAllPendingEmployers();
    
    int updateEmployerStatus(int employerId, ApprovalStatus status);
    
    // Job Posting Monitoring
    List<JobPost> findAllJobPosts();
    JobPost findJobPostById(int jobId);
    int updateJobPostStatus(int jobId, JobStatus status);
    List<JobPost> findJobPostsByStatus(JobStatus status);
    List<JobPost> findJobPostsByEmployerId(int employerId);
    
    // Premium Plans & Subscription Management
    List<Subscription> findAllSubscriptions();
    int updateSubscription(int subscriptionId, Subscription subscription);
    List<Employer> findPremiumEmployers();
    List<JobSeeker> findPremiumJobSeekers();
    List<Payment> findAllPayments();
    List<Payment> findPaymentsByStatus(PaymentStatus status);
    Map<String, Object> getPaymentStatistics();
    
    // Reports & Analytics
    Report saveReport(Report report);
    List<Report> findAllReports();
    Report findReportById(int reportId);
    Map<String, Object> getDashboardAnalytics();
    Map<String, Object> getJobAnalytics();
    Map<String, Object> getApplicationAnalytics();
    Map<String, Object> getUserAnalytics();
    
    // User Management & Activity Monitoring
    List<User> findAllUsers();
    User findUserById(int userId);
    int updateUserStatus(int userId, UserStatus status);
    List<Application> findAllApplications();
    Map<String, Object> getApplicationStatistics();
    
    // Additional Management
    List<Employer> findEmployersByApprovalStatus(ApprovalStatus status);
    List<JobSeeker> findAllJobSeekers();
    
    // Enhanced methods returning Maps with joined data for admin views
    List<Map<String, Object>> findEmployersWithDetails(ApprovalStatus status);
    List<Map<String, Object>> findAllEmployersWithDetails();
    List<Map<String, Object>> findPendingEmployersWithDetails();
    List<Map<String, Object>> findPremiumEmployersWithDetails();
    List<Map<String, Object>> findJobSeekersWithDetails();
    List<Map<String, Object>> findPremiumJobSeekersWithDetails();
    List<Map<String, Object>> findApplicationsWithDetails();
    List<Map<String, Object>> findPaymentsWithDetails();
    List<Map<String, Object>> findPaymentsByStatusWithDetails(PaymentStatus status);
    List<Map<String, Object>> findAllReportsWithDetails();
}
