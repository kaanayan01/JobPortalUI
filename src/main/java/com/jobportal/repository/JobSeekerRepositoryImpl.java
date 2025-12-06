package com.jobportal.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.User;
import com.jobportal.model.enums.PaymentStatus;
import com.jobportal.model.enums.SubscriptionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JobSeekerRepositoryImpl implements JobSeekerRepository {

	private final JdbcTemplate jdbc;
	
	@Autowired
	private ObjectMapper mapper;
	

	public JobSeekerRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	private JobSeeker mapRow(ResultSet rs, int rowNum) throws SQLException {
		JobSeeker js = new JobSeeker();
		js.setJobSeekerId(rs.getInt("job_seeker_id"));
		js.setUserId(rs.getInt("user_id"));
		js.setResumeFile(rs.getString("resume_file"));
		String skillsJson = rs.getString("skills");
		try {
		    if (skillsJson != null && !skillsJson.isEmpty()) {
		        List<String> skills = mapper.readValue(skillsJson, new TypeReference<List<String>>() {});
		        js.setSkills(skills);
		    } else {
		        js.setSkills(new ArrayList<>());
		    }
		} catch (Exception e) {
		   
		}
		js.setSubscriptionType(SubscriptionType.valueOf(rs.getString("subscription_type")));

		Date premiumDate = rs.getDate("premium_expiry");
		if (premiumDate != null) {
			js.setPremiumExpiry(premiumDate.toLocalDate());
		}

		js.setLastPaymentId(rs.getInt("last_payment_id"));
		js.setDeleted(rs.getBoolean("is_deleted"));
		return js;
	}

	@Override
	public JobSeeker create(JobSeeker jobSeeker) {

		System.out.println(jobSeeker.getSkills());
	    String sql = "INSERT INTO job_seeker(" +
	            "user_id, resume_file, skills, subscription_type, premium_expiry, last_payment_id, is_deleted" +
	            ") VALUES (?,?,?,?,?,?,?)";

	    // Convert skills list to JSON BEFORE lambda (so it's final / effectively final)
	    final String skillsJson;
	    try {
	        skillsJson = jobSeeker.getSkills() != null
	                ? mapper.writeValueAsString(jobSeeker.getSkills())
	                : "[]";
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to convert skills list to JSON", e);
	    }

	    KeyHolder keyHolder = new GeneratedKeyHolder();

	    jdbc.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

	        ps.setInt(1, jobSeeker.getUserId());
	        ps.setString(2, jobSeeker.getResumeFile());
	        ps.setString(3, skillsJson);
	        ps.setString(4, jobSeeker.getSubscriptionType().name());

	        if (jobSeeker.getPremiumExpiry() != null) {
	            ps.setDate(5, Date.valueOf(jobSeeker.getPremiumExpiry()));
	        } else {
	            ps.setNull(5, java.sql.Types.DATE);
	        }

	        if (jobSeeker.getLastPaymentId() != null) {
	            ps.setInt(6, jobSeeker.getLastPaymentId());
	        } else {
	            ps.setNull(6, java.sql.Types.INTEGER);
	        }

	        ps.setBoolean(7, jobSeeker.isDeleted());

	        return ps;
	    }, keyHolder);

	    // Set generated ID
	    if (keyHolder.getKey() != null) {
	        jobSeeker.setJobSeekerId(keyHolder.getKey().intValue());
	    }

	    return jobSeeker;
	}


	@Override
	public JobSeeker findById(int id) {
		try {
		String sql = "SELECT * FROM job_seeker WHERE job_seeker_id=?";
		return jdbc.queryForObject(sql, this::mapRow, id);
		}catch(DataAccessException e) {
			return null;
		}
	}
	
	@Override
	public JobSeeker IsUserId(int id) {
		
		String sql = "SELECT * FROM job_seeker WHERE user_id=?";
		try {
	        
	        return jdbc.queryForObject(sql, this::mapRow, id);
	       
	    } catch (org.springframework.dao.EmptyResultDataAccessException e) {
	       // throw new ResourceNotFoundException("JobSeeker not found");
	     return new JobSeeker();
	    }
		
	}

	@Override
	public List<JobSeeker> findAll() {
		String sql = "SELECT * FROM job_seeker WHERE is_deleted=0 ";
		return jdbc.query(sql, this::mapRow);
	}

	@Override
	public JobSeeker update(JobSeeker jobSeeker) {

	    String sql = "UPDATE job_seeker SET " +
	            "resume_file=?, skills=?, subscription_type=?, premium_expiry=?, " +
	            "last_payment_id=?, is_deleted=? " +
	            "WHERE job_seeker_id=?";

	    // Convert skills list into JSON BEFORE lambda
	    final String skillsJson;
	    try {
	        skillsJson = jobSeeker.getSkills() != null
	                ? mapper.writeValueAsString(jobSeeker.getSkills())
	                : "[]";
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to convert skills list to JSON", e);
	    }

	    jdbc.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql);

	        ps.setString(1, jobSeeker.getResumeFile());
	        ps.setString(2, skillsJson);
	        ps.setString(3, jobSeeker.getSubscriptionType().name());

	        if (jobSeeker.getPremiumExpiry() != null) {
	            ps.setDate(4, Date.valueOf(jobSeeker.getPremiumExpiry()));
	        } else {
	            ps.setNull(4, java.sql.Types.DATE);
	        }

	        if (jobSeeker.getLastPaymentId() != null) {
	            ps.setInt(5, jobSeeker.getLastPaymentId());
	        } else {
	            ps.setNull(5, java.sql.Types.INTEGER);
	        }

	        ps.setBoolean(6, jobSeeker.isDeleted());
	        ps.setInt(7, jobSeeker.getJobSeekerId());

	        return ps;
	    });

	    return jobSeeker;
	}

	@Override
	public int softDelete(int id) {
		String sql = "UPDATE job_seeker SET is_deleted=1 WHERE job_seeker_id=?";
		return jdbc.update(sql, id);
	}

	@Override
	public void updateResume(int id, String relativePath) {

		String sql= "UPDATE job_seeker SET resume_file=? WHERE job_seeker_id=?";
        jdbc.update(sql, relativePath, id);

	}

	@Override
	public void updateSubscriptionType( int userId, LocalDateTime expiry, int paymentId) {
		String sql = "UPDATE job_seeker SET subscription_type = 'PREMIUM', premium_expiry = ?, last_payment_id = ?  WHERE user_id=?";
		jdbc.update(sql, expiry, paymentId, userId);
		
	}
}
