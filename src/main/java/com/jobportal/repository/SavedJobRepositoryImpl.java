package com.jobportal.repository;

import com.jobportal.model.SavedJob;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class SavedJobRepositoryImpl implements SavedJobRepository {

    private final JdbcTemplate jdbcTemplate;

    public SavedJobRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SavedJob> mapper = new RowMapper<>() {
        @Override
        public SavedJob mapRow(ResultSet rs, int rowNum) throws SQLException {
            SavedJob s = new SavedJob();
            s.setSavedId(rs.getInt("saved_id"));
            s.setJobSeekerId(rs.getInt("job_seeker_id"));
            s.setJobId(rs.getInt("job_id"));
            Timestamp ts = rs.getTimestamp("saved_date");
            if (ts != null) s.setSavedDate(ts.toLocalDateTime());
            return s;
        }
    };

    @Override
    public SavedJob save(SavedJob savedJob) {
        String sql = "INSERT INTO saved_job(job_seeker_id, job_id, saved_date) VALUES(?,?,?)";
        jdbcTemplate.update(sql, savedJob.getJobSeekerId(), savedJob.getJobId(),
                savedJob.getSavedDate() != null ? savedJob.getSavedDate() : java.time.LocalDateTime.now());
        return jdbcTemplate.queryForObject(
                "SELECT * FROM saved_job WHERE job_seeker_id=? AND job_id=? ORDER BY saved_date DESC LIMIT 1",
                new Object[]{savedJob.getJobSeekerId(), savedJob.getJobId()}, mapper);
    }

    @Override
    public SavedJob findById(int id) {
        String sql = "SELECT * FROM saved_job WHERE saved_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    @Override
    public List<SavedJob> findAll() {
        String sql = "SELECT * FROM saved_job";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<SavedJob> findByJobSeekerId(int jobSeekerId) {
        String sql = "SELECT * FROM saved_job WHERE job_seeker_id=?";
        return jdbcTemplate.query(sql, new Object[]{jobSeekerId}, mapper);
    }

    @Override
    public List<SavedJob> findByJobId(int jobId) {
        String sql = "SELECT * FROM saved_job WHERE job_id=?";
        return jdbcTemplate.query(sql, new Object[]{jobId}, mapper);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM saved_job WHERE saved_id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsByJobSeekerIdAndJobId(int jobSeekerId, int jobId) {
        String sql = "SELECT COUNT(*) FROM saved_job WHERE job_seeker_id=? AND job_id=?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{jobSeekerId, jobId}, Integer.class);
        return count != null && count > 0;
    }
}
