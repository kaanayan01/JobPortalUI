package com.jobportal.repository;

import com.jobportal.model.Application;
import com.jobportal.model.enums.ApplicationStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final JdbcTemplate jdbcTemplate;

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
    public List<Application> findByJobId(int jobId) {
        String sql = "SELECT * FROM application WHERE job_id=? AND is_deleted=0";
        return jdbcTemplate.query(sql, new Object[]{jobId}, mapper);
    }

    @Override
    public Application update(Application a) {
        String sql = "UPDATE application SET cover_letter=?, status=? WHERE application_id=?";
        jdbcTemplate.update(sql, a.getCoverLetter(), a.getStatus().name(), a.getApplicationId());
        return findById(a.getApplicationId());
    }

    @Override
    public void softDelete(int id) {
        String sql = "UPDATE application SET is_deleted=1 WHERE application_id=?";
        jdbcTemplate.update(sql, id);
    }
}
