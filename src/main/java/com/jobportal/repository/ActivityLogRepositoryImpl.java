package com.jobportal.repository;

import com.jobportal.model.ActivityLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ActivityLogRepositoryImpl implements ActivityLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public ActivityLogRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ActivityLog> logMapper = new RowMapper<>() {
        @Override
        public ActivityLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            ActivityLog log = new ActivityLog();
            log.setLogId(rs.getInt("log_id"));
            log.setUserId(rs.getInt("user_id"));
            log.setActivityType(rs.getString("activity_type"));
            log.setActivityDescription(rs.getString("activity_description"));

            Timestamp ts = rs.getTimestamp("activity_time");
            if (ts != null) {
                log.setActivityTime(ts.toLocalDateTime());
            }

            return log;
        }
    };

    @Override
    public ActivityLog save(ActivityLog log) {
        String sql = "INSERT INTO activity_log(user_id, activity_type, activity_description) VALUES(?,?,?)";
        jdbcTemplate.update(sql, log.getUserId(), log.getActivityType(), log.getActivityDescription());

        return jdbcTemplate.queryForObject(
                "SELECT * FROM activity_log WHERE user_id=? ORDER BY log_id DESC LIMIT 1",
                new Object[]{log.getUserId()},
                logMapper
        );
    }

    @Override
    public ActivityLog findById(int logId) {
        String sql = "SELECT * FROM activity_log WHERE log_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{logId}, logMapper);
    }

    @Override
    public List<ActivityLog> findAll() {
        String sql = "SELECT * FROM activity_log";
        return jdbcTemplate.query(sql, logMapper);
    }

    @Override
    public List<ActivityLog> findByUserId(int userId) {
        String sql = "SELECT * FROM activity_log WHERE user_id=?";
        return jdbcTemplate.query(sql, new Object[]{userId}, logMapper);
    }

    @Override
    public void delete(int logId) {
        String sql = "DELETE FROM activity_log WHERE log_id=?";
        jdbcTemplate.update(sql, logId);
    }
}
