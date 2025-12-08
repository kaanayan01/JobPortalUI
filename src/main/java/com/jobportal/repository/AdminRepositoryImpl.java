package com.jobportal.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.model.Admin;
import com.jobportal.model.Application;
import com.jobportal.model.Employer;
import com.jobportal.model.JobPost;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.Payment;
import com.jobportal.model.Report;
import com.jobportal.model.Subscription;
import com.jobportal.model.User;
import com.jobportal.model.enums.ApplicationStatus;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.model.enums.JobStatus;
import com.jobportal.model.enums.JobType;
import com.jobportal.model.enums.PaymentMethod;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.model.enums.PlanType;
import com.jobportal.model.enums.SubscriptionType;
import com.jobportal.model.enums.UserStatus;
import com.jobportal.model.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public AdminRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Admin> adminMapper = new RowMapper<>() {
        @Override
        public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
            Admin a = new Admin();
            a.setAdminId(rs.getInt("admin_id"));
            a.setUserId(rs.getInt("user_id"));
            return a;
        }
    };
    
    private final RowMapper<Employer> employerMapper = new RowMapper<>() {
        @Override
        public Employer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employer e = new Employer();
            e.setEmployerId(rs.getInt("employer_id"));
            e.setUserId(rs.getInt("user_id"));
            e.setContactEmail(rs.getString("contact_email"));
            e.setContactNumber(rs.getString("contact_number"));

            // Safe enum conversion
            String status = rs.getString("approval_status");
            e.setApprovalStatus(status != null ? ApprovalStatus.valueOf(status) : ApprovalStatus.PENDING);

            String sub = rs.getString("subscription_type");
            e.setSubscriptionType(sub != null ? SubscriptionType.valueOf(sub) : SubscriptionType.FREE);

            e.setLastPaymentId(rs.getInt("last_payment_id"));
            e.setDeleted(rs.getBoolean("is_deleted"));
            return e;
        }
    };
    
    // Enhanced employer mapper with JOINs for admin views
    private final RowMapper<Map<String, Object>> employerWithDetailsMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("employerId", rs.getInt("employer_id"));
        map.put("userId", rs.getInt("user_id"));
        map.put("contactEmail", rs.getString("contact_email"));
        map.put("contactNumber", rs.getString("contact_number"));
        map.put("approvalStatus", rs.getString("approval_status"));
        String subscriptionType = rs.getString("subscription_type");
        map.put("subscriptionType", subscriptionType);
        // planType is an alias that may or may not exist in the query
        // If it exists (in premium queries), use it; otherwise use subscription_type
        String planType = subscriptionType; // Default to subscription_type
        try {
            // Check if planType column exists using ResultSetMetaData
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            boolean planTypeExists = false;
            for (int i = 1; i <= columnCount; i++) {
                if ("planType".equalsIgnoreCase(metaData.getColumnLabel(i))) {
                    planTypeExists = true;
                    break;
                }
            }
            if (planTypeExists) {
                String pt = rs.getString("planType");
                if (pt != null) {
                    planType = pt;
                }
            }
        } catch (SQLException e) {
            // If there's any error, just use subscription_type
            planType = subscriptionType;
        }
        map.put("planType", planType);
        map.put("companyName", rs.getString("company_name"));
        map.put("email", rs.getString("email"));
        map.put("name", rs.getString("name"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            map.put("createdAt", createdAt.toLocalDateTime());
        }
        return map;
    };

    @Override
    public Admin save(Admin admin) {
        String sql = "INSERT INTO admin(user_id) VALUES(?)";
        jdbcTemplate.update(sql, admin.getUserId());
        return jdbcTemplate.queryForObject(
                "SELECT * FROM admin WHERE user_id=? ORDER BY admin_id DESC LIMIT 1",
                adminMapper,
                admin.getUserId()
        );
    }

    @Override
    public Admin findById(int adminId) {
        String sql = "SELECT * FROM admin WHERE admin_id=?";
        return jdbcTemplate.queryForObject(sql, adminMapper, adminId);
    }

    @Override
    public List<Admin> findAll() {
        String sql = "SELECT * FROM admin";
        return jdbcTemplate.query(sql, adminMapper);
    }

    @Override
    public List<Employer> findAllPendingEmployers() {
        String sql = "SELECT e.*, u.email, u.name, u.created_at, cp.company_name " +
                     "FROM employer e " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE e.approval_status='PENDING' AND e.is_deleted=0";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, employerWithDetailsMapper);
        return results.stream().map(map -> {
            Employer e = new Employer();
            e.setEmployerId((Integer) map.get("employerId"));
            e.setUserId((Integer) map.get("userId"));
            e.setContactEmail((String) map.get("contactEmail"));
            e.setContactNumber((String) map.get("contactNumber"));
            String status = (String) map.get("approvalStatus");
            e.setApprovalStatus(status != null ? ApprovalStatus.valueOf(status) : ApprovalStatus.PENDING);
            return e;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public int updateEmployerStatus(int employerId, ApprovalStatus status) {
        String sql = "UPDATE employer SET approval_status=? WHERE employer_id=?";
        return jdbcTemplate.update(sql, status.name(), employerId);
    }
    
    // ==========================================================
    // Job Posting Monitoring Row Mappers
    // ==========================================================
    private final RowMapper<JobPost> jobPostMapper = (rs, rowNum) -> {
        JobPost jp = new JobPost();
        jp.setJobId(rs.getInt("job_id"));
        jp.setEmployerId(rs.getInt("employer_id"));
        jp.setTitle(rs.getString("title"));
        jp.setDescription(rs.getString("description"));
        
        String json = rs.getString("requirements");
        try {
            if (json != null && !json.trim().isEmpty()) {
                List<String> reqList = objectMapper.readValue(json, new TypeReference<List<String>>() {});
                jp.setRequirements(reqList);
            } else {
                jp.setRequirements(new ArrayList<>());
            }
        } catch (Exception e) {
            jp.setRequirements(new ArrayList<>());
        }
        
        jp.setJobLocation(rs.getString("job_location"));
        jp.setSalary(rs.getString("salary"));
        jp.setJobType(JobType.valueOf(rs.getString("job_type")));
        jp.setStatus(JobStatus.valueOf(rs.getString("status")));
        
        Timestamp ts = rs.getTimestamp("posted_date");
        if (ts != null) {
            jp.setPostedDate(ts.toLocalDateTime());
        }
        
        return jp;
    };
    
    // ==========================================================
    // Job Posting Monitoring Methods
    // ==========================================================
    @Override
    public List<JobPost> findAllJobPosts() {
        String sql = "SELECT * FROM job_post ORDER BY posted_date DESC";
        return jdbcTemplate.query(sql, jobPostMapper);
    }
    
    @Override
    public JobPost findJobPostById(int jobId) {
        String sql = "SELECT * FROM job_post WHERE job_id=?";
        return jdbcTemplate.queryForObject(sql, jobPostMapper, jobId);
    }
    
    @Override
    public int updateJobPostStatus(int jobId, JobStatus status) {
        String sql = "UPDATE job_post SET status=? WHERE job_id=?";
        return jdbcTemplate.update(sql, status.name(), jobId);
    }
    
    @Override
    public List<JobPost> findJobPostsByStatus(JobStatus status) {
        String sql = "SELECT * FROM job_post WHERE status=? ORDER BY posted_date DESC";
        return jdbcTemplate.query(sql, jobPostMapper, status.name());
    }
    
    @Override
    public List<JobPost> findJobPostsByEmployerId(int employerId) {
        String sql = "SELECT * FROM job_post WHERE employer_id=? ORDER BY posted_date DESC";
        return jdbcTemplate.query(sql, jobPostMapper, employerId);
    }
    
    // ==========================================================
    // Premium Plans & Subscription Management Row Mappers
    // ==========================================================
    private final RowMapper<Subscription> subscriptionMapper = (rs, rowNum) -> {
        Subscription s = new Subscription();
        s.setSubscriptionId(rs.getInt("subscription_id"));
        String plan = rs.getString("plan_type");
        s.setPlanType(plan != null ? PlanType.valueOf(plan) : null);
        String userType = rs.getString("user_type");
        s.setUserType(userType != null ? UserType.valueOf(userType) : null);
        s.setDuration(rs.getInt("duration"));
        s.setPrice(rs.getBigDecimal("price"));
        return s;
    };
    
    private final RowMapper<Payment> paymentMapper = (rs, rowNum) -> {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setSubscriptionId(rs.getInt("subscription_id"));
        p.setAmount(rs.getBigDecimal("amount"));
        String method = rs.getString("payment_method");
        p.setPaymentMethod(method != null ? PaymentMethod.valueOf(method) : null);
        p.setOrderId(rs.getString("order_id"));
        p.setTransactionId(rs.getString("transaction_id"));
        String status = rs.getString("status");
        p.setStatus(status != null ? PaymentStatus.valueOf(status) : PaymentStatus.PENDING);
        Timestamp ts = rs.getTimestamp("payment_date");
        if (ts != null) {
            p.setPaymentDate(ts.toLocalDateTime());
        }
        return p;
    };
    
    // Enhanced payment mapper with JOINs for admin views
    private final RowMapper<Map<String, Object>> paymentWithDetailsMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("paymentId", rs.getInt("payment_id"));
        map.put("userId", rs.getInt("user_id"));
        map.put("subscriptionId", rs.getInt("subscription_id"));
        map.put("amount", rs.getBigDecimal("amount"));
        // Use aliases from SQL query: paymentType or fallback to payment_method
        String paymentMethod = null;
        try {
            // Check if paymentType alias exists
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            boolean paymentTypeExists = false;
            for (int i = 1; i <= columnCount; i++) {
                if ("paymentType".equalsIgnoreCase(metaData.getColumnLabel(i))) {
                    paymentTypeExists = true;
                    break;
                }
            }
            if (paymentTypeExists) {
                paymentMethod = rs.getString("paymentType");
            }
        } catch (SQLException e) {
            // If error, fallback to payment_method
        }
        if (paymentMethod == null) {
            paymentMethod = rs.getString("payment_method");
        }
        map.put("paymentMethod", paymentMethod);
        map.put("paymentType", paymentMethod); // Also set paymentType for frontend compatibility
        // Use paymentStatus alias from SQL query
        map.put("paymentStatus", rs.getString("paymentStatus"));
        map.put("status", rs.getString("paymentStatus")); // Also set status for frontend compatibility
        map.put("orderId", rs.getString("order_id"));
        map.put("transactionId", rs.getString("transaction_id"));
        // Use aliases from SQL query
        map.put("userEmail", rs.getString("userEmail"));
        map.put("userName", rs.getString("userName"));
        Timestamp paymentDate = rs.getTimestamp("payment_date");
        if (paymentDate != null) {
            map.put("paymentDate", paymentDate.toLocalDateTime());
            map.put("createdAt", paymentDate.toLocalDateTime());
        }
        return map;
    };
    
    private final RowMapper<JobSeeker> jobSeekerMapper = (rs, rowNum) -> {
        JobSeeker js = new JobSeeker();
        js.setJobSeekerId(rs.getInt("job_seeker_id"));
        js.setUserId(rs.getInt("user_id"));
        js.setResumeFile(rs.getString("resume_file"));
        String skillsJson = rs.getString("skills");
        try {
            if (skillsJson != null && !skillsJson.isEmpty()) {
                List<String> skills = objectMapper.readValue(skillsJson, new TypeReference<List<String>>() {});
                js.setSkills(skills);
            } else {
                js.setSkills(new ArrayList<>());
            }
        } catch (Exception e) {
            js.setSkills(new ArrayList<>());
        }
        js.setSubscriptionType(SubscriptionType.valueOf(rs.getString("subscription_type")));
        java.sql.Date premiumDate = rs.getDate("premium_expiry");
        if (premiumDate != null) {
            js.setPremiumExpiry(premiumDate.toLocalDate());
        }
        js.setLastPaymentId(rs.getInt("last_payment_id"));
        js.setDeleted(rs.getBoolean("is_deleted"));
        return js;
    };
    
    // Enhanced job seeker mapper with JOINs for admin views
    private final RowMapper<Map<String, Object>> jobSeekerWithDetailsMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("jobSeekerId", rs.getInt("job_seeker_id"));
        map.put("userId", rs.getInt("user_id"));
        map.put("resumeFile", rs.getString("resume_file"));
        String subscriptionType = rs.getString("subscription_type");
        map.put("subscriptionType", subscriptionType);
        // planType is an alias that may or may not exist in the query
        // If it exists (in premium queries), use it; otherwise use subscription_type
        String planType = subscriptionType; // Default to subscription_type
        try {
            // Check if planType column exists using ResultSetMetaData
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            boolean planTypeExists = false;
            for (int i = 1; i <= columnCount; i++) {
                if ("planType".equalsIgnoreCase(metaData.getColumnLabel(i))) {
                    planTypeExists = true;
                    break;
                }
            }
            if (planTypeExists) {
                String pt = rs.getString("planType");
                if (pt != null) {
                    planType = pt;
                }
            }
        } catch (SQLException e) {
            // If there's any error, just use subscription_type
            planType = subscriptionType;
        }
        map.put("planType", planType);
        String name = rs.getString("name");
        map.put("name", name);
        if (name != null) {
            String[] nameParts = name.split(" ", 2);
            map.put("firstName", nameParts.length > 0 ? nameParts[0] : name);
            map.put("lastName", nameParts.length > 1 ? nameParts[1] : "");
        }
        map.put("email", rs.getString("email"));
        map.put("phoneNumber", null); // Not in schema, but frontend expects it
        map.put("yearsOfExperience", null); // Not in schema, but frontend expects it
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            map.put("createdAt", createdAt.toLocalDateTime());
        }
        return map;
    };
    
    // ==========================================================
    // Premium Plans & Subscription Management Methods
    // ==========================================================
    @Override
    public List<Subscription> findAllSubscriptions() {
        String sql = "SELECT * FROM subscription ORDER BY subscription_id";
        return jdbcTemplate.query(sql, subscriptionMapper);
    }
    
    @Override
    public int updateSubscription(int subscriptionId, Subscription subscription) {
        String sql = "UPDATE subscription SET plan_type=?, user_type=?, duration=?, price=? WHERE subscription_id=?";
        return jdbcTemplate.update(sql,
                subscription.getPlanType().name(),
                subscription.getUserType().name(),
                subscription.getDuration(),
                subscription.getPrice(),
                subscriptionId);
    }
    
    @Override
    public List<Employer> findPremiumEmployers() {
        String sql = "SELECT e.*, u.email, u.name, u.created_at, cp.company_name " +
                     "FROM employer e " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE e.subscription_type='PREMIUM' AND e.is_deleted=0";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, employerWithDetailsMapper);
        return results.stream().map(map -> {
            Employer e = new Employer();
            e.setEmployerId((Integer) map.get("employerId"));
            e.setUserId((Integer) map.get("userId"));
            e.setContactEmail((String) map.get("contactEmail"));
            e.setContactNumber((String) map.get("contactNumber"));
            String status = (String) map.get("approvalStatus");
            e.setApprovalStatus(status != null ? ApprovalStatus.valueOf(status) : ApprovalStatus.PENDING);
            String sub = (String) map.get("subscriptionType");
            e.setSubscriptionType(sub != null ? SubscriptionType.valueOf(sub) : SubscriptionType.FREE);
            return e;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<JobSeeker> findPremiumJobSeekers() {
        String sql = "SELECT js.*, u.email, u.name, u.created_at " +
                     "FROM job_seeker js " +
                     "LEFT JOIN users u ON js.user_id = u.user_id " +
                     "WHERE js.subscription_type='PREMIUM' AND js.is_deleted=0";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, jobSeekerWithDetailsMapper);
        return results.stream().map(map -> {
            JobSeeker js = new JobSeeker();
            js.setJobSeekerId((Integer) map.get("jobSeekerId"));
            js.setUserId((Integer) map.get("userId"));
            js.setResumeFile((String) map.get("resumeFile"));
            String sub = (String) map.get("subscriptionType");
            js.setSubscriptionType(sub != null ? SubscriptionType.valueOf(sub) : SubscriptionType.FREE);
            return js;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Payment> findAllPayments() {
        String sql = "SELECT p.*, u.email, u.name " +
                     "FROM payments p " +
                     "LEFT JOIN users u ON p.user_id = u.user_id " +
                     "ORDER BY p.payment_date DESC";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, paymentWithDetailsMapper);
        return results.stream().map(map -> {
            Payment p = new Payment();
            p.setPaymentId((Integer) map.get("paymentId"));
            p.setUserId((Integer) map.get("userId"));
            p.setSubscriptionId((Integer) map.get("subscriptionId"));
            p.setAmount((BigDecimal) map.get("amount"));
            String method = (String) map.get("paymentMethod");
            p.setPaymentMethod(method != null ? PaymentMethod.valueOf(method) : null);
            p.setOrderId((String) map.get("orderId"));
            p.setTransactionId((String) map.get("transactionId"));
            String status = (String) map.get("paymentStatus");
            p.setStatus(status != null ? PaymentStatus.valueOf(status) : PaymentStatus.PENDING);
            if (map.get("paymentDate") != null) {
                p.setPaymentDate((java.time.LocalDateTime) map.get("paymentDate"));
            }
            return p;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Payment> findPaymentsByStatus(PaymentStatus status) {
        String sql = "SELECT p.*, u.email, u.name " +
                     "FROM payments p " +
                     "LEFT JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.status=? ORDER BY p.payment_date DESC";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, paymentWithDetailsMapper, status.name());
        return results.stream().map(map -> {
            Payment p = new Payment();
            p.setPaymentId((Integer) map.get("paymentId"));
            p.setUserId((Integer) map.get("userId"));
            p.setSubscriptionId((Integer) map.get("subscriptionId"));
            p.setAmount((BigDecimal) map.get("amount"));
            String method = (String) map.get("paymentMethod");
            p.setPaymentMethod(method != null ? PaymentMethod.valueOf(method) : null);
            p.setOrderId((String) map.get("orderId"));
            p.setTransactionId((String) map.get("transactionId"));
            String paymentStatus = (String) map.get("paymentStatus");
            p.setStatus(paymentStatus != null ? PaymentStatus.valueOf(paymentStatus) : PaymentStatus.PENDING);
            if (map.get("paymentDate") != null) {
                p.setPaymentDate((java.time.LocalDateTime) map.get("paymentDate"));
            }
            return p;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getPaymentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String totalRevenueSql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status='SUCCESS'";
        BigDecimal totalRevenue = jdbcTemplate.queryForObject(totalRevenueSql, BigDecimal.class);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        String totalPaymentsSql = "SELECT COUNT(*) FROM payments";
        Integer totalPayments = jdbcTemplate.queryForObject(totalPaymentsSql, Integer.class);
        stats.put("totalPayments", totalPayments != null ? totalPayments : 0);
        
        String successPaymentsSql = "SELECT COUNT(*) FROM payments WHERE status='SUCCESS'";
        Integer successPayments = jdbcTemplate.queryForObject(successPaymentsSql, Integer.class);
        stats.put("successPayments", successPayments != null ? successPayments : 0);
        
        String failedPaymentsSql = "SELECT COUNT(*) FROM payments WHERE status='FAILED'";
        Integer failedPayments = jdbcTemplate.queryForObject(failedPaymentsSql, Integer.class);
        stats.put("failedPayments", failedPayments != null ? failedPayments : 0);
        
        String pendingPaymentsSql = "SELECT COUNT(*) FROM payments WHERE status='PENDING'";
        Integer pendingPayments = jdbcTemplate.queryForObject(pendingPaymentsSql, Integer.class);
        stats.put("pendingPayments", pendingPayments != null ? pendingPayments : 0);
        
        double successRate = totalPayments != null && totalPayments > 0 
            ? (double) successPayments / totalPayments * 100 
            : 0.0;
        stats.put("successRate", successRate);
        
        return stats;
    }
    
    // ==========================================================
    // Reports & Analytics Row Mappers
    // ==========================================================
    private final RowMapper<Report> reportMapper = (rs, rowNum) -> {
        Report r = new Report();
        r.setReportId(rs.getInt("report_id"));
        r.setReportType(rs.getString("report_type"));
        r.setContent(rs.getString("content"));
        Timestamp ts = rs.getTimestamp("generated_date");
        if (ts != null) {
            r.setGeneratedDate(ts.toLocalDateTime());
        }
        r.setGeneratedBy(rs.getInt("generated_by"));
        return r;
    };
    
    // Enhanced report mapper for admin views
    private final RowMapper<Map<String, Object>> reportWithDetailsMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("reportId", rs.getInt("report_id"));
        map.put("reportType", rs.getString("report_type"));
        map.put("content", rs.getString("content"));
        map.put("description", rs.getString("content")); // Frontend expects description
        map.put("reportName", rs.getString("report_type")); // Frontend expects reportName
        Timestamp ts = rs.getTimestamp("generated_date");
        if (ts != null) {
            map.put("generatedDate", ts.toLocalDateTime());
            map.put("createdAt", ts.toLocalDateTime()); // Frontend expects createdAt
        }
        map.put("generatedBy", rs.getInt("generated_by"));
        return map;
    };
    
    private final RowMapper<Application> applicationMapper = (rs, rowNum) -> {
        Application a = new Application();
        a.setApplicationId(rs.getInt("application_id"));
        a.setJobId(rs.getInt("job_id"));
        a.setJobSeekerId(rs.getInt("job_seeker_id"));
        a.setCoverLetter(rs.getString("cover_letter"));
        Timestamp ts = rs.getTimestamp("applied_date");
        if (ts != null) {
            a.setAppliedDate(ts.toLocalDateTime());
        }
        a.setStatus(ApplicationStatus.valueOf(rs.getString("status")));
        a.setDeleted(rs.getBoolean("is_deleted"));
        return a;
    };
    
    // Enhanced application mapper with JOINs for admin views
    private final RowMapper<Map<String, Object>> applicationWithDetailsMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("applicationId", rs.getInt("application_id"));
        map.put("jobId", rs.getInt("job_id"));
        map.put("jobSeekerId", rs.getInt("job_seeker_id"));
        map.put("coverLetter", rs.getString("cover_letter"));
        map.put("status", rs.getString("status"));
        // Use the aliases from the SQL query
        map.put("jobTitle", rs.getString("jobTitle"));
        map.put("jobSeekerName", rs.getString("jobSeekerName"));
        map.put("employerName", rs.getString("employerName"));
        Timestamp appliedDate = rs.getTimestamp("applied_date");
        if (appliedDate != null) {
            map.put("appliedDate", appliedDate.toLocalDateTime());
        }
        return map;
    };
    
    private final RowMapper<User> userMapper = (rs, rowNum) -> {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setEmail(rs.getString("email"));
        u.setName(rs.getString("name"));
        u.setPassword(rs.getString("password"));
        u.setUserType(UserType.valueOf(rs.getString("user_type")));
        u.setProfileImage(rs.getString("profile_image"));
        u.setStatus(UserStatus.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            u.setCreatedAt(ts.toLocalDateTime());
        }
        return u;
    };
    
    // ==========================================================
    // Reports & Analytics Methods
    // ==========================================================
    @Override
    public Report saveReport(Report report) {
        String sql = "INSERT INTO report(report_type, content, generated_by) VALUES(?,?,?)";
        jdbcTemplate.update(sql, report.getReportType(), report.getContent(), report.getGeneratedBy());
        return jdbcTemplate.queryForObject(
                "SELECT * FROM report WHERE generated_by=? ORDER BY report_id DESC LIMIT 1",
                reportMapper,
                report.getGeneratedBy()
        );
    }
    
    @Override
    public List<Report> findAllReports() {
        String sql = "SELECT * FROM report ORDER BY generated_date DESC";
        return jdbcTemplate.query(sql, reportMapper);
    }
    
    @Override
    public List<Map<String, Object>> findAllReportsWithDetails() {
        String sql = "SELECT * FROM report ORDER BY generated_date DESC";
        return jdbcTemplate.query(sql, reportWithDetailsMapper);
    }
    
    @Override
    public Report findReportById(int reportId) {
        String sql = "SELECT * FROM report WHERE report_id=?";
        return jdbcTemplate.queryForObject(sql, reportMapper, reportId);
    }
    
    @Override
    public Map<String, Object> getDashboardAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        String totalUsersSql = "SELECT COUNT(*) FROM users WHERE status!='DELETED'";
        Integer totalUsers = jdbcTemplate.queryForObject(totalUsersSql, Integer.class);
        analytics.put("totalUsers", totalUsers != null ? totalUsers : 0);
        
        String totalEmployersSql = "SELECT COUNT(*) FROM employer WHERE is_deleted=0";
        Integer totalEmployers = jdbcTemplate.queryForObject(totalEmployersSql, Integer.class);
        analytics.put("totalEmployers", totalEmployers != null ? totalEmployers : 0);
        
        String activeEmployersSql = "SELECT COUNT(*) FROM employer WHERE approval_status='APPROVED' AND is_deleted=0";
        Integer activeEmployers = jdbcTemplate.queryForObject(activeEmployersSql, Integer.class);
        analytics.put("activeEmployers", activeEmployers != null ? activeEmployers : 0);
        
        String totalJobSeekersSql = "SELECT COUNT(*) FROM job_seeker WHERE is_deleted=0";
        Integer totalJobSeekers = jdbcTemplate.queryForObject(totalJobSeekersSql, Integer.class);
        analytics.put("totalJobSeekers", totalJobSeekers != null ? totalJobSeekers : 0);
        
        String totalJobsSql = "SELECT COUNT(*) FROM job_post WHERE status!='DELETED'";
        Integer totalJobs = jdbcTemplate.queryForObject(totalJobsSql, Integer.class);
        analytics.put("totalJobs", totalJobs != null ? totalJobs : 0);
        analytics.put("jobsPosted", totalJobs != null ? totalJobs : 0); // Frontend expects this
        
        String activeJobsSql = "SELECT COUNT(*) FROM job_post WHERE status='ACTIVE'";
        Integer activeJobs = jdbcTemplate.queryForObject(activeJobsSql, Integer.class);
        analytics.put("activeJobs", activeJobs != null ? activeJobs : 0);
        
        String totalApplicationsSql = "SELECT COUNT(*) FROM application WHERE is_deleted=0";
        Integer totalApplications = jdbcTemplate.queryForObject(totalApplicationsSql, Integer.class);
        analytics.put("totalApplications", totalApplications != null ? totalApplications : 0);
        
        String pendingApprovalsSql = "SELECT COUNT(*) FROM employer WHERE approval_status='PENDING' AND is_deleted=0";
        Integer pendingApprovals = jdbcTemplate.queryForObject(pendingApprovalsSql, Integer.class);
        analytics.put("pendingApprovals", pendingApprovals != null ? pendingApprovals : 0);
        
        String premiumEmployersSql = "SELECT COUNT(*) FROM employer WHERE subscription_type='PREMIUM' AND is_deleted=0";
        Integer premiumEmployers = jdbcTemplate.queryForObject(premiumEmployersSql, Integer.class);
        analytics.put("premiumEmployers", premiumEmployers != null ? premiumEmployers : 0);
        
        String premiumJobSeekersSql = "SELECT COUNT(*) FROM job_seeker WHERE subscription_type='PREMIUM' AND is_deleted=0";
        Integer premiumJobSeekers = jdbcTemplate.queryForObject(premiumJobSeekersSql, Integer.class);
        analytics.put("premiumJobSeekers", premiumJobSeekers != null ? premiumJobSeekers : 0);
        analytics.put("premiumUsers", (premiumEmployers != null ? premiumEmployers : 0) + (premiumJobSeekers != null ? premiumJobSeekers : 0)); // Frontend expects this
        
        String totalRevenueSql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status='SUCCESS'";
        BigDecimal totalRevenue = jdbcTemplate.queryForObject(totalRevenueSql, BigDecimal.class);
        analytics.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        analytics.put("totalPayments", totalRevenue != null ? totalRevenue : BigDecimal.ZERO); // Frontend expects this
        
        return analytics;
    }
    
    @Override
    public Map<String, Object> getJobAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        String totalJobsSql = "SELECT COUNT(*) FROM job_post WHERE status!='DELETED'";
        Integer totalJobs = jdbcTemplate.queryForObject(totalJobsSql, Integer.class);
        analytics.put("totalJobs", totalJobs != null ? totalJobs : 0);
        
        String activeJobsSql = "SELECT COUNT(*) FROM job_post WHERE status='ACTIVE'";
        Integer activeJobs = jdbcTemplate.queryForObject(activeJobsSql, Integer.class);
        analytics.put("activeJobs", activeJobs != null ? activeJobs : 0);
        
        String inactiveJobsSql = "SELECT COUNT(*) FROM job_post WHERE status='INACTIVE'";
        Integer inactiveJobs = jdbcTemplate.queryForObject(inactiveJobsSql, Integer.class);
        analytics.put("inactiveJobs", inactiveJobs != null ? inactiveJobs : 0);
        
        String fullTimeJobsSql = "SELECT COUNT(*) FROM job_post WHERE job_type='FULL_TIME' AND status!='DELETED'";
        Integer fullTimeJobs = jdbcTemplate.queryForObject(fullTimeJobsSql, Integer.class);
        analytics.put("fullTimeJobs", fullTimeJobs != null ? fullTimeJobs : 0);
        
        String partTimeJobsSql = "SELECT COUNT(*) FROM job_post WHERE job_type='PART_TIME' AND status!='DELETED'";
        Integer partTimeJobs = jdbcTemplate.queryForObject(partTimeJobsSql, Integer.class);
        analytics.put("partTimeJobs", partTimeJobs != null ? partTimeJobs : 0);
        
        String internshipJobsSql = "SELECT COUNT(*) FROM job_post WHERE job_type='INTERNSHIP' AND status!='DELETED'";
        Integer internshipJobs = jdbcTemplate.queryForObject(internshipJobsSql, Integer.class);
        analytics.put("internshipJobs", internshipJobs != null ? internshipJobs : 0);
        
        String jobsLast30DaysSql = "SELECT COUNT(*) FROM job_post WHERE posted_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
        Integer jobsLast30Days = jdbcTemplate.queryForObject(jobsLast30DaysSql, Integer.class);
        analytics.put("jobsLast30Days", jobsLast30Days != null ? jobsLast30Days : 0);
        
        return analytics;
    }
    
    @Override
    public Map<String, Object> getApplicationAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        String totalApplicationsSql = "SELECT COUNT(*) FROM application WHERE is_deleted=0";
        Integer totalApplications = jdbcTemplate.queryForObject(totalApplicationsSql, Integer.class);
        analytics.put("totalApplications", totalApplications != null ? totalApplications : 0);
        
        String appliedSql = "SELECT COUNT(*) FROM application WHERE status='APPLIED' AND is_deleted=0";
        Integer applied = jdbcTemplate.queryForObject(appliedSql, Integer.class);
        analytics.put("applied", applied != null ? applied : 0);
        
        String reviewedSql = "SELECT COUNT(*) FROM application WHERE status='REVIEWED' AND is_deleted=0";
        Integer reviewed = jdbcTemplate.queryForObject(reviewedSql, Integer.class);
        analytics.put("reviewed", reviewed != null ? reviewed : 0);
        
        String shortlistedSql = "SELECT COUNT(*) FROM application WHERE status='SHORTLISTED' AND is_deleted=0";
        Integer shortlisted = jdbcTemplate.queryForObject(shortlistedSql, Integer.class);
        analytics.put("shortlisted", shortlisted != null ? shortlisted : 0);
        
        String rejectedSql = "SELECT COUNT(*) FROM application WHERE status='REJECTED' AND is_deleted=0";
        Integer rejected = jdbcTemplate.queryForObject(rejectedSql, Integer.class);
        analytics.put("rejected", rejected != null ? rejected : 0);
        
        String selectedSql = "SELECT COUNT(*) FROM application WHERE status='SELECTED' AND is_deleted=0";
        Integer selected = jdbcTemplate.queryForObject(selectedSql, Integer.class);
        analytics.put("selected", selected != null ? selected : 0);
        
        String applicationsLast30DaysSql = "SELECT COUNT(*) FROM application WHERE applied_date >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND is_deleted=0";
        Integer applicationsLast30Days = jdbcTemplate.queryForObject(applicationsLast30DaysSql, Integer.class);
        analytics.put("applicationsLast30Days", applicationsLast30Days != null ? applicationsLast30Days : 0);
        
        return analytics;
    }
    
    @Override
    public Map<String, Object> getUserAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        String totalUsersSql = "SELECT COUNT(*) FROM users WHERE status!='DELETED'";
        Integer totalUsers = jdbcTemplate.queryForObject(totalUsersSql, Integer.class);
        analytics.put("totalUsers", totalUsers != null ? totalUsers : 0);
        
        String activeUsersSql = "SELECT COUNT(*) FROM users WHERE status='ACTIVE'";
        Integer activeUsers = jdbcTemplate.queryForObject(activeUsersSql, Integer.class);
        analytics.put("activeUsers", activeUsers != null ? activeUsers : 0);
        
        String pendingUsersSql = "SELECT COUNT(*) FROM users WHERE status='PENDING'";
        Integer pendingUsers = jdbcTemplate.queryForObject(pendingUsersSql, Integer.class);
        analytics.put("pendingUsers", pendingUsers != null ? pendingUsers : 0);
        
        String adminUsersSql = "SELECT COUNT(*) FROM users WHERE user_type='ADMIN' AND status!='DELETED'";
        Integer adminUsers = jdbcTemplate.queryForObject(adminUsersSql, Integer.class);
        analytics.put("adminUsers", adminUsers != null ? adminUsers : 0);
        
        String employerUsersSql = "SELECT COUNT(*) FROM users WHERE user_type='EMPLOYER' AND status!='DELETED'";
        Integer employerUsers = jdbcTemplate.queryForObject(employerUsersSql, Integer.class);
        analytics.put("employerUsers", employerUsers != null ? employerUsers : 0);
        
        String jobSeekerUsersSql = "SELECT COUNT(*) FROM users WHERE user_type='JOB_SEEKER' AND status!='DELETED'";
        Integer jobSeekerUsers = jdbcTemplate.queryForObject(jobSeekerUsersSql, Integer.class);
        analytics.put("jobSeekerUsers", jobSeekerUsers != null ? jobSeekerUsers : 0);
        
        String usersLast30DaysSql = "SELECT COUNT(*) FROM users WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND status!='DELETED'";
        Integer usersLast30Days = jdbcTemplate.queryForObject(usersLast30DaysSql, Integer.class);
        analytics.put("usersLast30Days", usersLast30Days != null ? usersLast30Days : 0);
        
        return analytics;
    }
    
    // ==========================================================
    // User Management & Activity Monitoring Methods
    // ==========================================================
    @Override
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users WHERE status!='DELETED' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userMapper);
    }
    
    @Override
    public User findUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id=?";
        return jdbcTemplate.queryForObject(sql, userMapper, userId);
    }
    
    @Override
    public int updateUserStatus(int userId, UserStatus status) {
        String sql = "UPDATE users SET status=? WHERE user_id=?";
        return jdbcTemplate.update(sql, status.name(), userId);
    }
    
    @Override
    public List<Application> findAllApplications() {
        String sql = "SELECT a.*, jp.title, u.name as job_seeker_name, cp.company_name " +
                     "FROM application a " +
                     "LEFT JOIN job_post jp ON a.job_id = jp.job_id " +
                     "LEFT JOIN job_seeker js ON a.job_seeker_id = js.job_seeker_id " +
                     "LEFT JOIN users u ON js.user_id = u.user_id " +
                     "LEFT JOIN employer e ON jp.employer_id = e.employer_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE a.is_deleted=0 ORDER BY a.applied_date DESC";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, applicationWithDetailsMapper);
        return results.stream().map(map -> {
            Application a = new Application();
            a.setApplicationId((Integer) map.get("applicationId"));
            a.setJobId((Integer) map.get("jobId"));
            a.setJobSeekerId((Integer) map.get("jobSeekerId"));
            a.setCoverLetter((String) map.get("coverLetter"));
            String status = (String) map.get("status");
            a.setStatus(status != null ? ApplicationStatus.valueOf(status) : ApplicationStatus.APPLIED);
            if (map.get("appliedDate") != null) {
                a.setAppliedDate((java.time.LocalDateTime) map.get("appliedDate"));
            }
            return a;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getApplicationStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String totalApplicationsSql = "SELECT COUNT(*) FROM application WHERE is_deleted=0";
        Integer totalApplications = jdbcTemplate.queryForObject(totalApplicationsSql, Integer.class);
        stats.put("totalApplications", totalApplications != null ? totalApplications : 0);
        
        String appliedSql = "SELECT COUNT(*) FROM application WHERE status='APPLIED' AND is_deleted=0";
        Integer applied = jdbcTemplate.queryForObject(appliedSql, Integer.class);
        stats.put("applied", applied != null ? applied : 0);
        
        String reviewedSql = "SELECT COUNT(*) FROM application WHERE status='REVIEWED' AND is_deleted=0";
        Integer reviewed = jdbcTemplate.queryForObject(reviewedSql, Integer.class);
        stats.put("reviewed", reviewed != null ? reviewed : 0);
        
        String shortlistedSql = "SELECT COUNT(*) FROM application WHERE status='SHORTLISTED' AND is_deleted=0";
        Integer shortlisted = jdbcTemplate.queryForObject(shortlistedSql, Integer.class);
        stats.put("shortlisted", shortlisted != null ? shortlisted : 0);
        
        String rejectedSql = "SELECT COUNT(*) FROM application WHERE status='REJECTED' AND is_deleted=0";
        Integer rejected = jdbcTemplate.queryForObject(rejectedSql, Integer.class);
        stats.put("rejected", rejected != null ? rejected : 0);
        
        String selectedSql = "SELECT COUNT(*) FROM application WHERE status='SELECTED' AND is_deleted=0";
        Integer selected = jdbcTemplate.queryForObject(selectedSql, Integer.class);
        stats.put("selected", selected != null ? selected : 0);
        
        return stats;
    }
    
    // ==========================================================
    // Additional Management Methods
    // ==========================================================
    @Override
    public List<Employer> findEmployersByApprovalStatus(ApprovalStatus status) {
        String sql = "SELECT e.*, u.email, u.name, u.created_at, cp.company_name " +
                     "FROM employer e " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE e.approval_status=? AND e.is_deleted=0";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, employerWithDetailsMapper, status.name());
        return results.stream().map(map -> {
            Employer e = new Employer();
            e.setEmployerId((Integer) map.get("employerId"));
            e.setUserId((Integer) map.get("userId"));
            e.setContactEmail((String) map.get("contactEmail"));
            e.setContactNumber((String) map.get("contactNumber"));
            String approvalStatus = (String) map.get("approvalStatus");
            e.setApprovalStatus(approvalStatus != null ? ApprovalStatus.valueOf(approvalStatus) : ApprovalStatus.PENDING);
            String sub = (String) map.get("subscriptionType");
            e.setSubscriptionType(sub != null ? SubscriptionType.valueOf(sub) : SubscriptionType.FREE);
            return e;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<JobSeeker> findAllJobSeekers() {
        String sql = "SELECT js.*, u.email, u.name, u.created_at " +
                     "FROM job_seeker js " +
                     "LEFT JOIN users u ON js.user_id = u.user_id " +
                     "WHERE js.is_deleted=0 ORDER BY js.job_seeker_id DESC";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, jobSeekerWithDetailsMapper);
        return results.stream().map(map -> {
            JobSeeker js = new JobSeeker();
            js.setJobSeekerId((Integer) map.get("jobSeekerId"));
            js.setUserId((Integer) map.get("userId"));
            js.setResumeFile((String) map.get("resumeFile"));
            String sub = (String) map.get("subscriptionType");
            js.setSubscriptionType(sub != null ? SubscriptionType.valueOf(sub) : SubscriptionType.FREE);
            return js;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    // Enhanced methods returning Maps with joined data
    @Override
    public List<Map<String, Object>> findEmployersWithDetails(ApprovalStatus status) {
        String sql = "SELECT e.*, u.email, u.name, u.created_at, cp.company_name " +
                     "FROM employer e " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE e.approval_status=? AND e.is_deleted=0";
        return jdbcTemplate.query(sql, employerWithDetailsMapper, status.name());
    }
    
    @Override
    public List<Map<String, Object>> findAllEmployersWithDetails() {
        String sql = "SELECT e.*, u.email, u.name, u.created_at, cp.company_name " +
                     "FROM employer e " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE e.is_deleted=0 ORDER BY e.employer_id DESC";
        return jdbcTemplate.query(sql, employerWithDetailsMapper);
    }
    
    @Override
    public List<Map<String, Object>> findPendingEmployersWithDetails() {
        String sql = "SELECT e.*, u.email, u.name, u.created_at, cp.company_name " +
                     "FROM employer e " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE e.approval_status='PENDING' AND e.is_deleted=0";
        return jdbcTemplate.query(sql, employerWithDetailsMapper);
    }
    
    @Override
    public List<Map<String, Object>> findPremiumEmployersWithDetails() {
        String sql = "SELECT e.*, u.email, u.name, u.created_at, cp.company_name, e.subscription_type as planType " +
                     "FROM employer e " +
                     "LEFT JOIN users u ON e.user_id = u.user_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE e.subscription_type='PREMIUM' AND e.is_deleted=0";
        return jdbcTemplate.query(sql, employerWithDetailsMapper);
    }
    
    @Override
    public List<Map<String, Object>> findJobSeekersWithDetails() {
        String sql = "SELECT js.*, u.email, u.name, u.created_at " +
                     "FROM job_seeker js " +
                     "LEFT JOIN users u ON js.user_id = u.user_id " +
                     "WHERE js.is_deleted=0 ORDER BY js.job_seeker_id DESC";
        return jdbcTemplate.query(sql, jobSeekerWithDetailsMapper);
    }
    
    @Override
    public List<Map<String, Object>> findPremiumJobSeekersWithDetails() {
        String sql = "SELECT js.*, u.email, u.name, u.created_at, js.subscription_type as planType " +
                     "FROM job_seeker js " +
                     "LEFT JOIN users u ON js.user_id = u.user_id " +
                     "WHERE js.subscription_type='PREMIUM' AND js.is_deleted=0";
        return jdbcTemplate.query(sql, jobSeekerWithDetailsMapper);
    }
    
    @Override
    public List<Map<String, Object>> findApplicationsWithDetails() {
        String sql = "SELECT a.*, jp.title as jobTitle, u.name as jobSeekerName, cp.company_name as employerName " +
                     "FROM application a " +
                     "LEFT JOIN job_post jp ON a.job_id = jp.job_id " +
                     "LEFT JOIN job_seeker js ON a.job_seeker_id = js.job_seeker_id " +
                     "LEFT JOIN users u ON js.user_id = u.user_id " +
                     "LEFT JOIN employer e ON jp.employer_id = e.employer_id " +
                     "LEFT JOIN company_profile cp ON e.employer_id = cp.employer_id " +
                     "WHERE a.is_deleted=0 ORDER BY a.applied_date DESC";
        return jdbcTemplate.query(sql, applicationWithDetailsMapper);
    }
    
    @Override
    public List<Map<String, Object>> findPaymentsWithDetails() {
        String sql = "SELECT p.*, u.email as userEmail, u.name as userName, p.status as paymentStatus, p.payment_method as paymentType " +
                     "FROM payments p " +
                     "LEFT JOIN users u ON p.user_id = u.user_id " +
                     "ORDER BY p.payment_date DESC";
        return jdbcTemplate.query(sql, paymentWithDetailsMapper);
    }
    
    @Override
    public List<Map<String, Object>> findPaymentsByStatusWithDetails(PaymentStatus status) {
        String sql = "SELECT p.*, u.email as userEmail, u.name as userName, p.status as paymentStatus, p.payment_method as paymentType " +
                     "FROM payments p " +
                     "LEFT JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.status=? ORDER BY p.payment_date DESC";
        return jdbcTemplate.query(sql, paymentWithDetailsMapper, status.name());
    }
    
}
