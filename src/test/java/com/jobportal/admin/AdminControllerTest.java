package com.jobportal.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.controller.AdminController;
import com.jobportal.dto.GenericResponse;
import com.jobportal.model.Employer;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
    }

    // ========== 1️⃣ TEST: GET Dashboard Analytics ==========
    @Test
    void testGetDashboardAnalytics_Success() throws Exception {
        // Arrange
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", 100);
        analytics.put("totalEmployers", 50);
        analytics.put("totalJobSeekers", 50);
        analytics.put("jobsPosted", 200);
        analytics.put("totalPayments", new BigDecimal("50000.00"));
        analytics.put("pendingApprovals", 5);
        analytics.put("premiumEmployers", 10);
        analytics.put("premiumJobSeekers", 15);

        when(adminService.getDashboardAnalytics()).thenReturn(analytics);

        // Act & Assert
        mockMvc.perform(get("/api/admins/analytics/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Dashboard analytics retrieved successfully"))
                .andExpect(jsonPath("$.data.totalUsers").value(100))
                .andExpect(jsonPath("$.data.totalEmployers").value(50))
                .andExpect(jsonPath("$.data.jobsPosted").value(200))
                .andExpect(jsonPath("$.data.pendingApprovals").value(5));

        verify(adminService, times(1)).getDashboardAnalytics();
    }

    // ========== 2️⃣ TEST: GET All Employers ==========
    @Test
    void testGetAllEmployers_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> employers = new ArrayList<>();
        Map<String, Object> employer1 = new HashMap<>();
        employer1.put("employerId", 1);
        employer1.put("companyName", "Tech Corp");
        employer1.put("email", "contact@techcorp.com");
        employer1.put("approvalStatus", "APPROVED");
        employers.add(employer1);

        Map<String, Object> employer2 = new HashMap<>();
        employer2.put("employerId", 2);
        employer2.put("companyName", "Dev Solutions");
        employer2.put("email", "info@devsolutions.com");
        employer2.put("approvalStatus", "PENDING");
        employers.add(employer2);

        when(adminService.getAllEmployersWithDetails()).thenReturn(employers);

        // Act & Assert
        mockMvc.perform(get("/api/admins/employers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("All employers retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].employerId").value(1))
                .andExpect(jsonPath("$.data[0].companyName").value("Tech Corp"))
                .andExpect(jsonPath("$.data[1].employerId").value(2))
                .andExpect(jsonPath("$.data[1].companyName").value("Dev Solutions"));

        verify(adminService, times(1)).getAllEmployersWithDetails();
    }

    // ========== 3️⃣ TEST: PUT Update Employer Status ==========
    @Test
    void testUpdateEmployerStatus_Success() throws Exception {
        // Arrange
        int employerId = 1;
        ApprovalStatus status = ApprovalStatus.APPROVED;
        
        Employer updatedEmployer = new Employer();
        updatedEmployer.setEmployerId(employerId);
        updatedEmployer.setApprovalStatus(status);

        when(adminService.approveOrRejectEmployer(employerId, status)).thenReturn(updatedEmployer);

        // Act & Assert
        mockMvc.perform(put("/api/admins/employer/{id}/status", employerId)
                .param("status", "APPROVED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employer status updated successfully to APPROVED"))
                .andExpect(jsonPath("$.data.employerId").value(1))
                .andExpect(jsonPath("$.data.approvalStatus").value("APPROVED"));

        verify(adminService, times(1)).approveOrRejectEmployer(employerId, status);
    }

    @Test
    void testUpdateEmployerStatus_Reject() throws Exception {
        // Arrange
        int employerId = 2;
        ApprovalStatus status = ApprovalStatus.REJECTED;
        
        Employer updatedEmployer = new Employer();
        updatedEmployer.setEmployerId(employerId);
        updatedEmployer.setApprovalStatus(status);

        when(adminService.approveOrRejectEmployer(employerId, status)).thenReturn(updatedEmployer);

        // Act & Assert
        mockMvc.perform(put("/api/admins/employer/{id}/status", employerId)
                .param("status", "REJECTED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employer status updated successfully to REJECTED"))
                .andExpect(jsonPath("$.data.employerId").value(2))
                .andExpect(jsonPath("$.data.approvalStatus").value("REJECTED"));

        verify(adminService, times(1)).approveOrRejectEmployer(employerId, status);
    }

    // ========== 4️⃣ TEST: GET All Applications ==========
    @Test
    void testGetAllApplications_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> applications = new ArrayList<>();
        Map<String, Object> app1 = new HashMap<>();
        app1.put("applicationId", 1);
        app1.put("jobId", 10);
        app1.put("jobSeekerId", 5);
        app1.put("jobTitle", "Software Engineer");
        app1.put("jobSeekerName", "John Doe");
        app1.put("employerName", "Tech Corp");
        app1.put("status", "APPLIED");
        applications.add(app1);

        Map<String, Object> app2 = new HashMap<>();
        app2.put("applicationId", 2);
        app2.put("jobId", 11);
        app2.put("jobSeekerId", 6);
        app2.put("jobTitle", "Data Analyst");
        app2.put("jobSeekerName", "Jane Smith");
        app2.put("employerName", "Data Inc");
        app2.put("status", "SHORTLISTED");
        applications.add(app2);

        when(adminService.getApplicationsWithDetails()).thenReturn(applications);

        // Act & Assert
        mockMvc.perform(get("/api/admins/applications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("All job applications retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].applicationId").value(1))
                .andExpect(jsonPath("$.data[0].jobTitle").value("Software Engineer"))
                .andExpect(jsonPath("$.data[0].jobSeekerName").value("John Doe"))
                .andExpect(jsonPath("$.data[1].applicationId").value(2))
                .andExpect(jsonPath("$.data[1].status").value("SHORTLISTED"));

        verify(adminService, times(1)).getApplicationsWithDetails();
    }

    // ========== 5️⃣ TEST: GET Payment Statistics ==========
    @Test
    void testGetPaymentStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", new BigDecimal("100000.00"));
        stats.put("totalPayments", 150);
        stats.put("successPayments", 140);
        stats.put("failedPayments", 5);
        stats.put("pendingPayments", 5);
        stats.put("successRate", 93.33);

        when(adminService.getPaymentStatistics()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/api/admins/payments/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Payment statistics retrieved successfully"))
                .andExpect(jsonPath("$.data.totalPayments").value(150))
                .andExpect(jsonPath("$.data.successPayments").value(140))
                .andExpect(jsonPath("$.data.failedPayments").value(5))
                .andExpect(jsonPath("$.data.pendingPayments").value(5))
                .andExpect(jsonPath("$.data.successRate").value(93.33));

        verify(adminService, times(1)).getPaymentStatistics();
    }
}

