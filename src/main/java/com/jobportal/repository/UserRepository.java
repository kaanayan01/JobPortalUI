package com.jobportal.repository;

import com.jobportal.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    boolean emailExists(String email);

    User create(User user);

    Optional<User> findById(int id);

    List<User> findAll();

    User update(User user);

    int delete(int id);
    
    User findByEmail(String email);
    
    void updateProfileImage(int userId, String relativePath);
}
