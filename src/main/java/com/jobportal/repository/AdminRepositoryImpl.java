package com.jobportal.repository;

import com.jobportal.model.Admin;
import com.jobportal.model.CompanyProfile;
import com.jobportal.model.Employer;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.model.enums.SubscriptionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private final JdbcTemplate jdbcTemplate;

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
    
    private final RowMapper<CompanyProfile> companyProfileMapper = (rs, rowNum) -> {
        CompanyProfile cp = new CompanyProfile();
        cp.setProfileId(rs.getInt("profile_id"));
        cp.setEmployerId(rs.getInt("employer_id"));
        cp.setCompanyName(rs.getString("company_name"));
        cp.setIndustry(rs.getString("industry"));
        cp.setAddress(rs.getString("address"));
        cp.setDescription(rs.getString("description"));
        cp.setLogo(rs.getString("logo"));
        cp.setWebsite(rs.getString("website"));
        return cp;
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

    @Override
    public Admin save(Admin admin) {
        String sql = "INSERT INTO admin(user_id) VALUES(?)";
        jdbcTemplate.update(sql, admin.getUserId());
        return jdbcTemplate.queryForObject(
                "SELECT * FROM admin WHERE user_id=? ORDER BY admin_id DESC LIMIT 1",
                new Object[]{admin.getUserId()},
                adminMapper
        );
    }

    @Override
    public Admin findById(int adminId) {
        String sql = "SELECT * FROM admin WHERE admin_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{adminId}, adminMapper);
    }

    @Override
    public List<Admin> findAll() {
        String sql = "SELECT * FROM admin";
        return jdbcTemplate.query(sql, adminMapper);
    }

    @Override
    public List<Employer> findAllPendingEmployers() {
        String sql = "SELECT * FROM employer WHERE approval_status='PENDING' AND is_deleted=0";
        return jdbcTemplate.query(sql, employerMapper);
    }

    @Override
    public int updateEmployerStatus(int employerId, ApprovalStatus status) {
        String sql = "UPDATE employer SET approval_status=? WHERE employer_id=?";
        return jdbcTemplate.update(sql, status.name(), employerId);
    }
    
    @Override
    public CompanyProfile findCompanyProfileByEmployer(int employerId) {
        String sql = "SELECT * FROM company_profile WHERE employer_id=? AND is_deleted=0";
        return jdbcTemplate.queryForObject(sql, companyProfileMapper, employerId);
    }
    
}
