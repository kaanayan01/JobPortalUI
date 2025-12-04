package com.jobportal.repository;

import com.jobportal.model.User;


import com.jobportal.model.enums.UserStatus;
import com.jobportal.model.enums.UserType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

	private final JdbcTemplate jdbc;

	public UserRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	private User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User u = new User();
		u.setUserId(rs.getInt("user_id"));
		u.setEmail(rs.getString("email"));
		u.setName(rs.getString("name"));
		u.setPassword(rs.getString("password"));
		u.setUserType(UserType.valueOf(rs.getString("user_type")));
		u.setProfileImage(rs.getString("profile_image"));
		u.setStatus(UserStatus.valueOf(rs.getString("status")));
		if (rs.getTimestamp("created_at") != null) {
			u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		}
		return u;
	}

	@Override
	public boolean emailExists(String email) {
		String sql = "SELECT COUNT(*) FROM users WHERE email=?";
		Integer count = jdbc.queryForObject(sql, Integer.class, email);
		return count != null && count > 0;
	}

	@Override
	public User create(User user) {
	    String sql = "INSERT INTO users(email, name, password, user_type, profile_image, status) VALUES (?,?,?,?,?,?)";

	    KeyHolder keyHolder = new GeneratedKeyHolder();

	    jdbc.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        ps.setString(1, user.getEmail());
	        ps.setString(2, user.getName());
	        ps.setString(3, user.getPassword());
	        ps.setString(4, user.getUserType().name());
	        ps.setString(5, user.getProfileImage());
	        ps.setString(6, user.getStatus() != null ? user.getStatus().name() : "PENDING");
	        return ps;
	    }, keyHolder);
	    System.out.println("-----------------------"+ keyHolder.getKey());
	    
	    int id=keyHolder.getKey().intValue();
	    user.setUserId(id);
	    // Return the generated user
	    return user;
	}
	@Override
	public Optional<User> findById(int id) {
	    String sql = "SELECT * FROM users WHERE user_id = ?";
	    try {
	        User user = jdbc.queryForObject(sql, this::mapRow, id);
	        return Optional.ofNullable(user);
	    } catch (org.springframework.dao.EmptyResultDataAccessException e) {
	        return Optional.empty();
	    }
	}

	@Override
	public List<User> findAll() {
		String sql = "SELECT * FROM users WHERE status!='DELETED'";
		return jdbc.query(sql, this::mapRow);
	}

	@Override
	public User update(User user) {
	    String sql = 
	        "UPDATE users SET email=?, name=?, profile_image=?, status=?, user_type=?, password=? WHERE user_id=?";

	    jdbc.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql);
	        ps.setString(1, user.getEmail());
	        ps.setString(2, user.getName());
	        ps.setString(3, user.getProfileImage() == null ? "null" : user.getProfileImage());
	        ps.setString(4, user.getStatus() != null ? user.getStatus().name() : "PENDING");
	        ps.setString(5, user.getUserType().name());
	        ps.setString(6, user.getPassword());
	        ps.setLong(7, user.getUserId());   // Needed for WHERE user_id=?
	        return ps;
	    });

	    // No generated keys in UPDATE — remove this
	    return user;
	}

		
	@Override
	public int delete(int id) {
		String sql = "UPDATE users SET status='DELETED' WHERE user_id=?";
		return jdbc.update(sql, id);
	}
	 public User findByEmail(String email) {
	        String sql = "SELECT * FROM users WHERE email = ?";
	        return jdbc.queryForObject(sql, (rs, rowNum) -> {
	            User u = new User();
	            u.setUserId(rs.getInt("user_id"));
	            u.setName(rs.getString("name"));
	            u.setEmail(rs.getString("email"));
	            u.setPassword(rs.getString("password"));
	            u.setUserType(UserType.valueOf(rs.getString("user_type")));
	            u.setStatus(UserStatus.valueOf(rs.getString("status")));
	            return u;
	        }, email);
	    }
	 
	 @Override
	    public void updateProfileImage(int userId, String relativePath) {
	        String sql = "UPDATE users SET profile_image = ? WHERE user_id = ?";
	        jdbc.update(sql, relativePath, userId);
	    }
}
