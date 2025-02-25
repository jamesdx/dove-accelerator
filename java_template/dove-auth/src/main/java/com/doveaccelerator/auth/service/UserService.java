package com.doveaccelerator.auth.service;

import com.doveaccelerator.auth.entity.User;
import com.doveaccelerator.auth.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    
    User updateUser(Long id, UserDTO userDTO);
    
    void deleteUser(Long id);
    
    Optional<User> getByUsername(String username);
    
    Optional<User> getById(Long id);
    
    Page<User> getAllUsers(Pageable pageable);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    void assignRole(Long userId, String roleName);
    
    void removeRole(Long userId, String roleName);
    
    void changePassword(Long userId, String oldPassword, String newPassword);
}