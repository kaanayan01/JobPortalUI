package com.jobportal.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.dto.ApplicationDTO;
import com.jobportal.model.Application;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.User;
import com.jobportal.model.enums.ApplicationStatus;
import com.jobportal.model.enums.SubscriptionType;
import com.jobportal.model.enums.UserStatus;
import com.jobportal.model.enums.UserType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    public ApplicationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Application> mapper = new RowMapper<>() {
        @Override
        public Application mapRow(ResultSet rs, int rowNum) throws SQLException {
            Application a = new Application();
            a.setApplicationId(rs.getInt("application_id"));
            a.setJobId(rs.getInt("job_id"));
            a.setJobSeekerId(rs.getInt("job_seeker_id"));
            a.setCoverLetter(rs.getString("cover_letter"));
            Timestamp ts = rs.getTimestamp("applied_date");
            if (ts != null) a.setAppliedDate(ts.toLocalDateTime());
            a.setStatus(ApplicationStatus.valueOf(rs.getString("status")));
            a.setDeleted(rs.getBoolean("is_deleted"));
            return a;
        }
    };

    @Override
    public Application save(Application a) {
        String sql = "INSERT INTO application(job_id, job_seeker_id, cover_letter, status, is_deleted) VALUES(?,?,?,?,?)";
        jdbcTemplate.update(sql, a.getJobId(), a.getJobSeekerId(), a.getCoverLetter(),
                a.getStatus().name(), a.isDeleted());
        return jdbcTemplate.queryForObject("SELECT * FROM application WHERE job_seeker_id=? AND job_id=? ORDER BY applied_date DESC LIMIT 1",
                new Object[]{a.getJobSeekerId(), a.getJobId()}, mapper);
    }

    @Override
    public Application findById(int id) {
        String sql = "SELECT * FROM application WHERE application_id=? AND is_deleted=0";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    @Override
    public List<Application> findAll() {
        String sql = "SELECT * FROM application WHERE is_deleted=0";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<Application> findByJobSeekerId(int jobSeekerId) {
        String sql = "SELECT * FROM application WHERE job_seeker_id=? AND is_deleted=0";
        return jdbcTemplate.query(sql, new Object[]{jobSeekerId}, mapper);
    }

    @Override
    public List<ApplicationDTO> findByJobId(int jobId) {

        String sql = """
            SELECT
                a.application_id,
                a.job_id,
                a.job_seeker_id,
                a.cover_letter,
                a.applied_date,
                a.status,
                a.is_deleted,

                js.job_seeker_id       AS js_job_seeker_id,
                js.user_id             AS js_user_id,
                js.skills,
                js.resume_file,
                js.subscription_type,
                js.premium_expiry,
                js.last_payment_id,
                js.is_deleted          AS js_is_deleted,

                u.user_id              AS u_user_id,
                u.email,
                u.name,
                u.password,
                u.user_type,
                u.profile_image,
                u.status               AS user_status,
                u.created_at
            FROM application a
            JOIN job_seeker js ON a.job_seeker_id = js.job_seeker_id
            JOIN users u       ON js.user_id      = u.user_id
            WHERE a.job_id = ?
              AND a.is_deleted = false
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            // ---- Application ----
            Application app = new Application();
            app.setApplicationId(rs.getInt("application_id"));
            app.setJobId(rs.getInt("job_id"));
            app.setJobSeekerId(rs.getInt("job_seeker_id"));
            app.setCoverLetter(rs.getString("cover_letter"));
            app.setAppliedDate(rs.getTimestamp("applied_date").toLocalDateTime());
            app.setStatus(ApplicationStatus.valueOf(rs.getString("status")));
            app.setDeleted(rs.getBoolean("is_deleted"));

            // ---- JobSeeker ----
            JobSeeker js = new JobSeeker();
            js.setJobSeekerId(rs.getInt("js_job_seeker_id"));
            js.setUserId(rs.getInt("js_user_id"));
//            js.setSkills(rs.getString("skills"));          // <-- add this line
            js.setResumeFile(rs.getString("resume_file"));
            js.setSubscriptionType(SubscriptionType.valueOf(rs.getString("subscription_type")));

            java.sql.Date premiumExpiry = rs.getDate("premium_expiry");
            if (premiumExpiry != null) {
                js.setPremiumExpiry(premiumExpiry.toLocalDate());
            }

            js.setLastPaymentId((Integer) rs.getObject("last_payment_id"));
            js.setDeleted(rs.getBoolean("js_is_deleted"));

            // ---- User ----
            User user = new User();
            user.setUserId(rs.getInt("u_user_id"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));    // now selected in SQL
            user.setUserType(UserType.valueOf(rs.getString("user_type")));
            user.setProfileImage(rs.getString("profile_image"));
            user.setStatus(UserStatus.valueOf(rs.getString("user_status")));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            // ---- Wrap in DTO ----
            ApplicationDTO details = new ApplicationDTO();
            details.setApplication(app);
            details.setJobSeeker(js);
            details.setUser(user);
            return details;
        }, jobId);
    }

    
    private String toJson(List<String> list) {
		try {
			return objectMapper.writeValueAsString(list != null ? list : new ArrayList<>());
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert requirements to JSON", e);
		}
	}
    

    @Override
    public Application update(Application a) {
        String sql = "UPDATE application SET  status=? WHERE application_id=?";
        jdbcTemplate.update(sql, a.getStatus().name(), a.getApplicationId());
        return findById(a.getApplicationId());
    }

    @Override
    public void softDelete(int id) {
        String sql = "UPDATE application SET is_deleted=1 WHERE application_id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public int findByJobIdAndJobSeekerIdOnDate(
            int jobId,
            int jobSeekerId,
            LocalDateTime now) {

        LocalDate startDate = now.toLocalDate();
        LocalDate endDate = startDate.plusDays(1);

        String sql =
            "SELECT COUNT(*) " +
            "FROM application " +
            "WHERE job_seeker_id = ? " +
            "AND is_deleted = false " +
            "AND applied_date >= ? " + 
            " AND applied_date < ?" ;

        return jdbcTemplate.queryForObject(
            sql,
            Integer.class,
           
            jobSeekerId,
            startDate.atStartOfDay(),   // 00:00:00
            endDate.atStartOfDay()      // next day 00:00:00
        );
    }

}
