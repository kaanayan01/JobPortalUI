package com.jobportal.repository;

import com.jobportal.model.Report;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReportRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Report> reportMapper = new RowMapper<>() {
        @Override
        public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
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
        }
    };

    @Override
    public Report save(Report report) {
        String sql = "INSERT INTO report(report_type, content, generated_by) VALUES(?,?,?)";
        jdbcTemplate.update(sql, report.getReportType(), report.getContent(), report.getGeneratedBy());

        return jdbcTemplate.queryForObject(
                "SELECT * FROM report WHERE generated_by=? ORDER BY report_id DESC LIMIT 1",
                new Object[]{report.getGeneratedBy()},
                reportMapper
        );
    }

    @Override
    public Report findById(int reportId) {
        String sql = "SELECT * FROM report WHERE report_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{reportId}, reportMapper);
    }

    @Override
    public List<Report> findAll() {
        String sql = "SELECT * FROM report";
        return jdbcTemplate.query(sql, reportMapper);
    }

    @Override
    public List<Report> findByAdminId(int adminId) {
        String sql = "SELECT * FROM report WHERE generated_by=?";
        return jdbcTemplate.query(sql, new Object[]{adminId}, reportMapper);
    }

    @Override
    public void update(Report report) {
        String sql = "UPDATE report SET report_type=?, content=? WHERE report_id=?";
        jdbcTemplate.update(sql, report.getReportType(), report.getContent(), report.getReportId());
    }

    @Override
    public void delete(int reportId) {
        String sql = "DELETE FROM report WHERE report_id=?";
        jdbcTemplate.update(sql, reportId);
    }
}
