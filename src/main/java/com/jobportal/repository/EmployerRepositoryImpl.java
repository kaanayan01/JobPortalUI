package com.jobportal.repository;

import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Employer;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.enums.ApprovalStatus;
import com.jobportal.model.enums.SubscriptionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

@Repository
public class EmployerRepositoryImpl implements EmployerRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployerRepositoryImpl.class);

	private final JdbcTemplate jdbc;

	public EmployerRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	private Employer mapRow(ResultSet rs, int rowNum) throws SQLException {
		Employer e = new Employer();
		e.setEmployerId(rs.getInt("employer_id"));
		e.setUserId(rs.getInt("user_id"));
		e.setContactEmail(rs.getString("contact_email"));
		e.setContactNumber(rs.getString("contact_number"));
		e.setApprovalStatus(ApprovalStatus.valueOf(rs.getString("approval_status")));
		e.setSubscriptionType(SubscriptionType.valueOf(rs.getString("subscription_type")));

		Date premiumDate = rs.getDate("premium_expiry");
		if (premiumDate != null) {
			e.setPremiumExpiry(premiumDate.toLocalDate());
		}

		e.setLastPaymentId(rs.getInt("last_payment_id"));
		e.setDeleted(rs.getBoolean("is_deleted"));
		return e;
	}

	@Override
	public Employer create(Employer employer) {
		String sql = "INSERT INTO employer(user_id, contact_email, contact_number, approval_status, subscription_type, premium_expiry, last_payment_id, is_deleted) "
		           + "VALUES (?,?,?,?,?,?,?,?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbc.update(connection -> {
		    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		    ps.setInt(1, employer.getUserId()); // user_id
		    ps.setString(2, employer.getContactEmail());
		    ps.setString(3, employer.getContactNumber());
		    ps.setString(4, employer.getApprovalStatus().name());
		    ps.setString(5, employer.getSubscriptionType().name());

		    if (employer.getPremiumExpiry() != null) {
		        ps.setDate(6, Date.valueOf(employer.getPremiumExpiry()));
		    } else {
		        ps.setNull(6, java.sql.Types.DATE);
		    }

		    if (employer.getLastPaymentId() != null) {
		        ps.setInt(7, employer.getLastPaymentId());
		    } else {
		        ps.setNull(7, java.sql.Types.INTEGER);
		    }

		    ps.setBoolean(8, employer.isDeleted()); // do NOT insert employer_id
		    return ps;
		}, keyHolder);

		employer.setEmployerId(keyHolder.getKey().intValue());
		return employer;

	}

	@Override
	public Employer findById(int id) {
		String sql = "SELECT * FROM employer WHERE employer_id=?";
		 try {
	        	return jdbc.queryForObject(sql, this::mapRow, id);
	        }
	        catch(DataAccessException e) {
	        	logger.info("Employer not found for id :" , id);
	        	
	        	throw new ResourceNotFoundException("Employer does not exist");
	        }
	}

	@Override
	public List<Employer> findAll() {
		String sql = "SELECT * FROM employer WHERE is_deleted=0";
		return jdbc.query(sql, this::mapRow);
	}

	@Override
	public Employer update(Employer employer) {
		
		String sql = "UPDATE employer SET contact_email=?, contact_number=?, approval_status=?, " +
		             "subscription_type=?, premium_expiry=?, last_payment_id=? WHERE employer_id=?";

		jdbc.update(sql, ps -> {
			ps.setString(1, employer.getContactEmail());
			ps.setString(2, employer.getContactNumber());
			ps.setString(3, employer.getApprovalStatus().name());
			ps.setString(4, employer.getSubscriptionType().name());
			
			if (employer.getPremiumExpiry() != null) {
				ps.setDate(5, Date.valueOf(employer.getPremiumExpiry()));
			} else {
				ps.setNull(5, java.sql.Types.DATE);
			}

			if (employer.getLastPaymentId() != null) {
				ps.setInt(6, employer.getLastPaymentId());
			} else {
				ps.setNull(6, java.sql.Types.INTEGER);
			}

			ps.setInt(7, employer.getEmployerId());
		});

		return employer; // return updated entity
	}


	@Override
	public int softDelete(int id) {
		String sql = "UPDATE employer SET is_deleted=1 WHERE employer_id=?";
		return jdbc.update(sql, id);
	}

	@Override
	public List<Employer> findAllApproved() {
		String sql = "SELECT * FROM employer WHERE approval_status='APPROVED' AND is_deleted=0";
		return jdbc.query(sql, this::mapRow);
	}

	@Override
	public List<Employer> findAllPending() {
		String sql = "SELECT * FROM employer WHERE approval_status='PENDING' AND is_deleted=0";
		return jdbc.query(sql, this::mapRow);
	}

	@Override
	public Employer isUserId(int id) {
		String sql = "SELECT * FROM employer WHERE user_id=?";
		try {
	        
	        return jdbc.queryForObject(sql, this::mapRow, id);
	       
	    } catch (org.springframework.dao.EmptyResultDataAccessException e) {
	       // throw new ResourceNotFoundException("JobSeeker not found");
	     return new Employer();
	    }
	}

	@Override
	public void updateSubscriptionType(int userId, LocalDateTime expiry, int paymentId) {
		String sql = "UPDATE employer SET subscription_type = ?, premium_expiry = ?, last_payment_id = ?  WHERE user_id =?";
		jdbc.update(sql, "PREMIUM", expiry, paymentId, userId);
		
	}
}
