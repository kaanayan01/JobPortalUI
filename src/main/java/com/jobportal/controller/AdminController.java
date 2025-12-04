package com.jobportal.controller;

import com.jobportal.dto.GenericResponse;
import com.jobportal.model.ActivityLog;
import com.jobportal.model.Admin;
import com.jobportal.model.Application;
import com.jobportal.model.CompanyProfile;
import com.jobportal.model.Employer;
import com.jobportal.model.JobPost;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.Payment;
import com.jobportal.model.Report;
import com.jobportal.model.Subscription;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.model.enums.JobStatus;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.service.AdminService;
import com.jobportal.utils.ResponseBuilder;
import com.jobportal.utils.Role;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Admin>> create(@RequestBody Admin admin) {
        Admin created = service.create(admin);
        return ResponseBuilder.success(created, "Admin created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Admin>> getById(@PathVariable int id) {
        Admin admin = service.findById(id);
        return ResponseBuilder.success(admin, "Admin retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<Admin>>> getAll() {
        List<Admin> admins = service.findAll();
        return ResponseBuilder.success(admins, "All admins retrieved successfully");
    }

    @GetMapping("/employers/pending")
    public ResponseEntity<GenericResponse<List<Employer>>> getPendingEmployers() {
        List<Employer> pending = service.listPendingEmployers();
        return ResponseBuilder.success(pending, "Pending employers retrieved successfully");
    }
    
//    @Role("ADMIN")
    @PutMapping("/employer/{id}/status")
    public ResponseEntity<GenericResponse<Employer>> updateEmployerStatus(
            @PathVariable int id,
            @RequestParam ApprovalStatus status) {

        Employer employer = service.approveOrRejectEmployer(id, status);
        return ResponseBuilder.success(employer, "Employer status updated successfully to " + status);
    }
    
    
    
    
    //-----------
    
// // Job Posting Monitoring & Compliance Endpoints
// 	@Role("ADMIN")
// 	@GetMapping("/jobs")
// 	public ResponseEntity<GenericResponse<List<JobPost>>> getAllJobPosts() {
// 		List<JobPost> jobs = service.getAllJobPosts();
// 		return ResponseBuilder.success(jobs, "All job posts retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/jobs/{id}")
// 	public ResponseEntity<GenericResponse<JobPost>> getJobPostById(@PathVariable int id) {
// 		JobPost jobPost = service.getJobPostById(id);
// 		return ResponseBuilder.success(jobPost, "Job post retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@PutMapping("/jobs/{id}/status")
// 	public ResponseEntity<GenericResponse<JobPost>> updateJobPostStatus(@PathVariable int id,
// 			@RequestParam JobStatus status) {
// 		JobPost jobPost = service.updateJobPostStatus(id, status);
// 		return ResponseBuilder.success(jobPost, "Job post status updated successfully to " + status);
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/jobs/active")
// 	public ResponseEntity<GenericResponse<List<JobPost>>> getActiveJobPosts() {
// 		List<JobPost> jobs = service.getJobPostsByStatus(JobStatus.ACTIVE);
// 		return ResponseBuilder.success(jobs, "Active job posts retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/jobs/inactive")
// 	public ResponseEntity<GenericResponse<List<JobPost>>> getInactiveJobPosts() {
// 		List<JobPost> jobs = service.getJobPostsByStatus(JobStatus.INACTIVE);
// 		return ResponseBuilder.success(jobs, "Inactive job posts retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/jobs/by-employer/{employerId}")
// 	public ResponseEntity<GenericResponse<List<JobPost>>> getJobPostsByEmployer(@PathVariable int employerId) {
// 		List<JobPost> jobs = service.getJobPostsByEmployerId(employerId);
// 		return ResponseBuilder.success(jobs, "Job posts by employer retrieved successfully");
// 	}
//
// 	// Premium Plans & Subscription Management Endpoints
// 	@Role("ADMIN")
// 	@GetMapping("/subscriptions")
// 	public ResponseEntity<GenericResponse<List<Subscription>>> getAllSubscriptions() {
// 		List<Subscription> subscriptions = service.getAllSubscriptions();
// 		return ResponseBuilder.success(subscriptions, "All subscription plans retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@PutMapping("/subscriptions/{id}")
// 	public ResponseEntity<GenericResponse<Subscription>> updateSubscription(@PathVariable int id,
// 			@RequestBody Subscription subscription) {
// 		Subscription updated = service.updateSubscription(id, subscription);
// 		return ResponseBuilder.success(updated, "Subscription plan updated successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/employers/premium")
// 	public ResponseEntity<GenericResponse<List<Employer>>> getPremiumEmployers() {
// 		List<Employer> employers = service.getPremiumEmployers();
// 		return ResponseBuilder.success(employers, "Premium employers retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/job-seekers/premium")
// 	public ResponseEntity<GenericResponse<List<JobSeeker>>> getPremiumJobSeekers() {
// 		List<JobSeeker> jobSeekers = service.getPremiumJobSeekers();
// 		return ResponseBuilder.success(jobSeekers, "Premium job seekers retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/payments")
// 	public ResponseEntity<GenericResponse<List<Payment>>> getAllPayments() {
// 		List<Payment> payments = service.getAllPayments();
// 		return ResponseBuilder.success(payments, "All payment transactions retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/payments/success")
// 	public ResponseEntity<GenericResponse<List<Payment>>> getSuccessfulPayments() {
// 		List<Payment> payments = service.getPaymentsByStatus(PaymentStatus.SUCCESS);
// 		return ResponseBuilder.success(payments, "Successful payment records retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/payments/stats")
// 	public ResponseEntity<GenericResponse<Map<String, Object>>> getPaymentStatistics() {
// 		Map<String, Object> stats = service.getPaymentStatistics();
// 		return ResponseBuilder.success(stats, "Payment statistics retrieved successfully");
// 	}
//
// 	// Reports & Analytics Endpoints
// 	@Role("ADMIN")
// 	@PostMapping("/reports/generate")
// 	public ResponseEntity<GenericResponse<Report>> generateReport(@RequestBody Report report) {
// 		Report generated = service.generateReport(report);
// 		return ResponseBuilder.success(generated, "Report generated successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/reports")
// 	public ResponseEntity<GenericResponse<List<Report>>> getAllReports() {
// 		List<Report> reports = service.getAllReports();
// 		return ResponseBuilder.success(reports, "All reports retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/reports/{id}")
// 	public ResponseEntity<GenericResponse<Report>> getReportById(@PathVariable int id) {
// 		Report report = service.getReportById(id);
// 		return ResponseBuilder.success(report, "Report retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/analytics/dashboard")
// 	public ResponseEntity<GenericResponse<Map<String, Object>>> getDashboardAnalytics() {
// 		Map<String, Object> analytics = service.getDashboardAnalytics();
// 		return ResponseBuilder.success(analytics, "Dashboard analytics retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/analytics/jobs")
// 	public ResponseEntity<GenericResponse<Map<String, Object>>> getJobAnalytics() {
// 		Map<String, Object> analytics = service.getJobAnalytics();
// 		return ResponseBuilder.success(analytics, "Job posting analytics retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/analytics/applications")
// 	public ResponseEntity<GenericResponse<Map<String, Object>>> getApplicationAnalytics() {
// 		Map<String, Object> analytics = service.getApplicationAnalytics();
// 		return ResponseBuilder.success(analytics, "Application analytics retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/analytics/users")
// 	public ResponseEntity<GenericResponse<Map<String, Object>>> getUserAnalytics() {
// 		Map<String, Object> analytics = service.getUserAnalytics();
// 		return ResponseBuilder.success(analytics, "User analytics retrieved successfully");
// 	}
//
// 	// User Management & Activity Monitoring Endpoints
// 	@Role("ADMIN")
// 	@GetMapping("/activity-logs")
// 	public ResponseEntity<GenericResponse<List<ActivityLog>>> getAllActivityLogs() {
// 		List<ActivityLog> logs = activityLogService.getAll();
// 		return ResponseBuilder.success(logs, "All activity logs retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/activity-logs/user/{userId}")
// 	public ResponseEntity<GenericResponse<List<ActivityLog>>> getActivityLogsByUser(@PathVariable int userId) {
// 		List<ActivityLog> logs = activityLogService.getByUserId(userId);
// 		return ResponseBuilder.success(logs, "Activity logs for user retrieved successfully");
// 	}
//
// 	// Additional Management Endpoints
// 	@Role("ADMIN")
// 	@GetMapping("/employers/approved")
// 	public ResponseEntity<GenericResponse<List<Employer>>> getApprovedEmployers() {
// 		List<Employer> employers = service.getEmployersByApprovalStatus(ApprovalStatus.APPROVED);
// 		return ResponseBuilder.success(employers, "Approved employers retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/employers/rejected")
// 	public ResponseEntity<GenericResponse<List<Employer>>> getRejectedEmployers() {
// 		List<Employer> employers = service.getEmployersByApprovalStatus(ApprovalStatus.REJECTED);
// 		return ResponseBuilder.success(employers, "Rejected employers retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/job-seekers")
// 	public ResponseEntity<GenericResponse<List<JobSeeker>>> getAllJobSeekers() {
// 		List<JobSeeker> jobSeekers = service.getAllJobSeekers();
// 		return ResponseBuilder.success(jobSeekers, "All job seekers retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/applications")
// 	public ResponseEntity<GenericResponse<List<Application>>> getAllApplications() {
// 		List<Application> applications = service.getAllApplications();
// 		return ResponseBuilder.success(applications, "All job applications retrieved successfully");
// 	}
//
// 	@Role("ADMIN")
// 	@GetMapping("/applications/stats")
// 	public ResponseEntity<GenericResponse<Map<String, Object>>> getApplicationStatistics() {
// 		Map<String, Object> stats = service.getApplicationStatistics();
// 		return ResponseBuilder.success(stats, "Application statistics retrieved successfully");
// 	}


    
    
  
}
