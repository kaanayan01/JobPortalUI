package com.jobportal.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.JobPost;
import com.jobportal.model.User;
import com.jobportal.model.enums.JobStatus;
import com.jobportal.model.enums.JobType;
import com.jobportal.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JobPostRepositoryImpl implements JobPostRepository {

	private static final Logger logger = LoggerFactory.getLogger(JobPostRepositoryImpl.class);
	private final JdbcTemplate jdbc;

	@Autowired
	private ObjectMapper mapper;

	public JobPostRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	// ==========================================================
	// Row Mapper
	// ==========================================================
	private final RowMapper<JobPost> rowMapper = (rs, rowNum) -> {

        JobPost jp = new JobPost();
        jp.setJobId(rs.getInt("job_id"));
        jp.setEmployerId(rs.getInt("employer_id"));
        jp.setTitle(rs.getString("title"));
        jp.setDescription(rs.getString("description"));

        // ---- JSON REQUIREMENTS ----
        String json = rs.getString("requirements");
        try {
            if (json != null && !json.trim().isEmpty()) {
                List<String> reqList = mapper.readValue(json, new TypeReference<List<String>>() {});
                jp.setRequirements(reqList);
            } else {
                jp.setRequirements(new ArrayList<>());
            }
        } catch (
	Exception e)
	{
		jp.setRequirements(new ArrayList<>());
	}

	jp.setJobLocation(rs.getString("job_location"));jp.setSalary(rs.getString("salary"));

	jp.setJobType(JobType.valueOf(rs.getString("job_type")));jp.setStatus(JobStatus.valueOf(rs.getString("status")));

	Timestamp ts = rs.getTimestamp("posted_date");if(ts!=null)
	{
		jp.setPostedDate(ts.toLocalDateTime());
	}

	return jp;
	};

	// ==========================================================
	// CREATE (return generated ID)
	// ==========================================================
	@Override
    public int create(JobPost jobPost) {

        String sql = "INSERT INTO job_post (employer_id, title, description, requirements, job_location, salary, job_type, status, posted_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String requirementsJson = toJson(jobPost.getRequirements());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update((PreparedStatementCreator) conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, jobPost.getEmployerId());
            ps.setString(2, jobPost.getTitle());
            ps.setString(3, jobPost.getDescription());
            ps.setString(4, requirementsJson);
            ps.setString(5, jobPost.getJobLocation());
            ps.setString(6, jobPost.getSalary());
            ps.setString(7, jobPost.getJobType().name());
            ps.setString(8, jobPost.getStatus().name());

            if (jobPost.getPostedDate() != null)
                ps.setTimestamp(9, Timestamp.valueOf(jobPost.getPostedDate()));
            else
                ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));

            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

	// ==========================================================
	// FIND BY ID
	// ==========================================================
	@Override
    public JobPost findById(int id) {
        String sql = "SELECT * FROM job_post WHERE job_id = ?";
        try {
        	return jdbc.queryForObject(sql, rowMapper, id);
        }
        catch(DataAccessException e) {
        	logger.info("Job post not found for id :" , id);
        	
        	throw new ResourceNotFoundException("Job Post does not exist");
        }
    }

	// ==========================================================
	// FIND ALL
	// ==========================================================

	
	@Override
	public List<JobPost> findAll(int pageSize, int pageNumber, String searchText) {
	    String sql = "SELECT * FROM job_post WHERE status != 'DELETED' " +
	                 "AND title LIKE ? ORDER BY job_id LIMIT ? OFFSET ?";

	    return jdbc.query(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql);
	        ps.setString(1, "%" + searchText + "%"); // Add wildcards for LIKE
	        ps.setInt(2, pageSize);
	               ps.setInt(3, (pageNumber) * pageSize); // Correct offset
	        return ps;
	    }, rowMapper);
	}


    // ==========================================================
    // UPDATE
    // ==========================================================
	@Override
    public int update(JobPost jobPost) {

        String sql = "UPDATE job_post SET title=?, description=?, requirements=?, job_location=?, salary=?, job_type=?, status=? WHERE job_id=?";

        String requirementsJson = toJson(jobPost.getRequirements());

        return jdbc.update(sql,
                jobPost.getTitle(),
                jobPost.getDescription(),
                requirementsJson,
                jobPost.getJobLocation(),
                jobPost.getSalary(),
                jobPost.getJobType().name(),
                jobPost.getStatus().name(),
                jobPost.getJobId()
        );
    }

	// ==========================================================
	// SOFT DELETE
	// ==========================================================
	@Override
    public int softDelete(int id) {
        String sql = "UPDATE job_post SET status='DELETED' WHERE job_id=?";
        return jdbc.update(sql, id);
    }

	// ==========================================================
	// FIND ALL ACTIVE
	// ==========================================================
	@Override
    public List<JobPost> findAllActive() {
        String sql = "SELECT * FROM job_post WHERE status='ACTIVE'";
        return jdbc.query(sql, rowMapper);
    }

	// ==========================================================
	// JSON utility
	// ==========================================================
	private String toJson(List<String> list) {
		try {
			return mapper.writeValueAsString(list != null ? list : new ArrayList<>());
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert requirements to JSON", e);
		}
	}

	@Override
	public List<JobPost> findByEmployerId(int employerId) {
		String sql = "SELECT * FROM job_post WHERE employer_id = ? AND status != 'DELETED'";
		return jdbc.query(sql, rowMapper, employerId);
	}

	@Override
	public int countJobs(String searchText) {
		String sql = "SELECT count(*) FROM job_post WHERE title like '%" + searchText + "%' AND status != 'DELETED'";
		
		return jdbc.queryForObject(sql, Integer.class);
	}

}
