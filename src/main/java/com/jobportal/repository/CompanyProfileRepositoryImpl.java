package com.jobportal.repository;

import com.jobportal.model.CompanyProfile;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class CompanyProfileRepositoryImpl implements CompanyProfileRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompanyProfileRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CompanyProfile> profileMapper = new RowMapper<>() {
        @Override
        public CompanyProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
            CompanyProfile p = new CompanyProfile();
            p.setProfileId(rs.getInt("profile_id"));
            p.setEmployerId(rs.getInt("employer_id"));
            p.setCompanyName(rs.getString("company_name"));
            p.setIndustry(rs.getString("industry"));
            p.setAddress(rs.getString("address"));
            p.setDescription(rs.getString("description"));
            p.setLogo(rs.getString("logo"));
            p.setWebsite(rs.getString("website"));
            p.setDeleted(rs.getBoolean("is_deleted"));
            return p;
        }
    };

    @Override
    public CompanyProfile save(CompanyProfile profile) {
        try {
        	 String sql = "INSERT INTO company_profile(" +
        	            "employer_id, company_name, industry, address, description, logo, website" +
        	            ") VALUES(?,?,?,?,?,?,?)";

        	    KeyHolder keyHolder = new GeneratedKeyHolder();

        	    jdbcTemplate.update(connection -> {
        	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        	        ps.setInt(1, profile.getEmployerId());
        	        ps.setString(2, profile.getCompanyName());
        	        ps.setString(3, profile.getIndustry());
        	        ps.setString(4, profile.getAddress());
        	        ps.setString(5, profile.getDescription());
        	        ps.setString(6, profile.getLogo());
        	        ps.setString(7, profile.getWebsite());
        	        return ps;
        	    }, keyHolder);

        	    // Set the generated primary key
        	    if (keyHolder.getKey() != null) {
        	        profile.setProfileId(keyHolder.getKey().intValue());
        	    }

        	    return profile;
        }catch(DataIntegrityViolationException e) {
            throw new RuntimeException("Company profile already exists for employerId: " + profile.getEmployerId());
        }
       
    }

    @Override
    public CompanyProfile findById(int profileId) {
        String sql = "SELECT * FROM company_profile WHERE profile_id=? AND is_deleted=0";
        return jdbcTemplate.queryForObject(sql, new Object[]{profileId}, profileMapper);
    }

    @Override
    public List<CompanyProfile> findAll() {
        String sql = "SELECT * FROM company_profile WHERE is_deleted=0";
        return jdbcTemplate.query(sql, profileMapper);
    }

    @Override
    public List<CompanyProfile> findByEmployerId(int employerId) {
        String sql = "SELECT * FROM company_profile WHERE employer_id=? AND is_deleted=0";
        return jdbcTemplate.query(sql, new Object[]{employerId}, profileMapper);
    }

    @Override
    public CompanyProfile update(CompanyProfile profile) {
        String sql = "UPDATE company_profile SET company_name=?, industry=?, address=?, description=?, logo=?, website=? WHERE profile_id=?";
        KeyHolder keyHolder = new GeneratedKeyHolder();

	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        ps.setInt(1, profile.getEmployerId());
	        ps.setString(2, profile.getCompanyName());
	        ps.setString(3, profile.getIndustry());
	        ps.setString(4, profile.getAddress());
	        ps.setString(5, profile.getDescription());
	        ps.setString(6, profile.getLogo());
	        ps.setString(7, profile.getWebsite());
	        return ps;
	    }, keyHolder);

	    // Set the generated primary key
	    if (keyHolder.getKey() != null) {
	        profile.setProfileId(keyHolder.getKey().intValue());
	    }

	    return profile;
    }

    @Override
    public void softDelete(int profileId) {
        String sql = "UPDATE company_profile SET is_deleted=1 WHERE profile_id=?";
        jdbcTemplate.update(sql, profileId);
    }

	
}
