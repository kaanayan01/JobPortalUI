 package com.jobportal.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.jobportal.dto.LoginResponse;
import com.jobportal.dto.UserDTO;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;

import com.jobportal.model.User;
import com.jobportal.model.enums.UserStatus;
import com.jobportal.repository.UserRepository;
import com.jobportal.utils.PasswordUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private FileStorageService fileStorageService;
  
    private PasswordUtil passwordUtils;
    
    		public UserService(UserRepository repository) {
    	        this.repository = repository;
    	    }

    	    public User create(User user) {
    	    	user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
    	        User createdUser = repository.create(user);
    	        return findUserByIdOrThrow(createdUser.getUserId());
    	    }

    	    public User update(User user) {
    	        repository.update(user);
    	        return findUserByIdOrThrow(user.getUserId());
    	    }

    	    public User findById(int id) {
    	        return findUserByIdOrThrow(id);
    	    }

    	    public List<User> findAll() {
    	        return repository.findAll();
    	    }

    	    public void delete(int id) {
    	        repository.delete(id);
    	    }
    	    private User findUserByIdOrThrow(int id) {
    	        return repository.findById(id)
    	                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    	    }
    	    
    	    /**
    	     * Stores the profile image and updates the user's profile_image column.
    	     * Returns updated User (fetched from DB).
    	     */
    	    public User updateProfileImage(int userId, MultipartFile file) {
    	        if (file == null || file.isEmpty()) {
    	            throw new BadRequestException("Profile image is empty");
    	        }

    	        // ensure user exists
    	        User user = repository.findById(userId)
    	                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    	        // prefix e.g. "user_12"
    	        String prefix = "user_" + userId;

    	        // save inside uploads/users/...
    	        String relativePath = fileStorageService.store(file, "users", prefix);

    	        // update DB
    	        repository.updateProfileImage(userId, relativePath);

    	        // fetch updated user and return
    	        return repository.findById(userId)
    	                .orElseThrow(() -> new ResourceNotFoundException("User not found after update: " + userId));
    	    }
    
    public LoginResponse login(String email, String password) {
        User user;
       
        try {
            user = repository.findByEmail(email);
            if(user.getStatus() == UserStatus.PENDING) {
            	user.setStatus(UserStatus.ACTIVE);
            }
            repository.update(user);
          
        } catch (Exception e) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!PasswordUtil.checkPassword(password,user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new BadRequestException("User is not active");
        }
        
        String token = jwtService.generateToken(user.getEmail(), user.getUserType().name());
        UserDTO userDTO = objectMapper.convertValue(user, UserDTO.class);
        return new LoginResponse(token, userDTO);
    }
}
