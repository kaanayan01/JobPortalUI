package com.jobportal.repository;

import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.SkillMatch;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class SkillMatchRepositoryImpl implements SkillMatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public SkillMatchRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SkillMatch> skillMatchMapper = new RowMapper<>() {
        @Override
        public SkillMatch mapRow(ResultSet rs, int rowNum) throws SQLException {
            SkillMatch sm = new SkillMatch();
            sm.setMatchId(rs.getInt("match_id"));
            sm.setJobSeekerId(rs.getInt("job_seeker_id"));
            sm.setJobId(rs.getInt("job_id"));
            sm.setMatchPercentage(rs.getDouble("match_percentage"));

            Timestamp ts = rs.getTimestamp("calculated_date");
            if (ts != null) {
                sm.setCalculatedDate(ts.toLocalDateTime());
            }

            return sm;
        }
    };

    @Override
    public SkillMatch save(SkillMatch skillMatch) {
        String sql = "INSERT INTO skill_match(job_seeker_id, job_id, match_percentage) VALUES(?,?,?)";
        jdbcTemplate.update(sql, skillMatch.getJobSeekerId(), skillMatch.getJobId(), skillMatch.getMatchPercentage());

        return jdbcTemplate.queryForObject(
                "SELECT * FROM skill_match WHERE job_seeker_id=? AND job_id=? ORDER BY match_id DESC LIMIT 1",
                new Object[]{skillMatch.getJobSeekerId(), skillMatch.getJobId()},
                skillMatchMapper
        );
    }

    @Override
    public SkillMatch findById(int matchId) {
        String sql = "SELECT * FROM skill_match WHERE match_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{matchId}, skillMatchMapper);
    }

    @Override
    public List<SkillMatch> findAll() {
        String sql = "SELECT * FROM skill_match";
        return jdbcTemplate.query(sql, skillMatchMapper);
    }

    @Override
    public List<SkillMatch> findByJobSeekerId(int jobSeekerId) {
        String sql = "SELECT * FROM skill_match WHERE job_seeker_id=?";
        return jdbcTemplate.query(sql, new Object[]{jobSeekerId}, skillMatchMapper);
    }

    @Override
    public List<SkillMatch> findByJobId(int jobId) {
        String sql = "SELECT * FROM skill_match WHERE job_id=?";
        return jdbcTemplate.query(sql, new Object[]{jobId}, skillMatchMapper);
    }
    
    @Override
    public SkillMatch findByJobIdAndSeekerId(int jobId, int seekerId) {
    	try {
        String sql = "SELECT * FROM skill_match WHERE job_id = ? AND job_seeker_id = ?";
        return jdbcTemplate.queryForObject(sql, skillMatchMapper, jobId, seekerId);
    	}catch(DataAccessException e) {
    		throw new ResourceNotFoundException("Seeker not found");
    	}
    }

    @Override
    public void update(SkillMatch skillMatch) {
        String sql = "UPDATE skill_match SET match_percentage=? WHERE match_id=?";
        jdbcTemplate.update(sql, skillMatch.getMatchPercentage(), skillMatch.getMatchId());
    }

    @Override
    public void delete(int matchId) {
        String sql = "DELETE FROM skill_match WHERE match_id=?";
        jdbcTemplate.update(sql, matchId);
    }
}
