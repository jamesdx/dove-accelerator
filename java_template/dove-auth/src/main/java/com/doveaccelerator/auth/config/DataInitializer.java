package com.doveaccelerator.auth.config;

import com.doveaccelerator.auth.entity.Permission;
import com.doveaccelerator.auth.entity.Role;
import com.doveaccelerator.auth.entity.User;
import com.doveaccelerator.auth.repository.PermissionRepository;
import com.doveaccelerator.auth.repository.RoleRepository;
import com.doveaccelerator.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing default data...");
        
        // Create default permissions
        createPermissionsIfNotExist();
        
        // Create default roles
        Role adminRole = createRoleIfNotExist("ADMIN", "Administrator with all privileges");
        Role userRole = createRoleIfNotExist("USER", "Regular user with limited privileges");
        
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            log.info("Creating admin user...");
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@doveaccelerator.com");
            adminUser.setFullName("System Administrator");
            adminUser.setEnabled(true);
            adminUser.getRoles().add(adminRole);
            
            userRepository.save(adminUser);
            log.info("Admin user created successfully");
        }
        
        log.info("Data initialization completed");
    }
    
    private Role createRoleIfNotExist(String name, String description) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }
        
        log.info("Creating role: {}", name);
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setActive(true);
        
        return roleRepository.save(role);
    }
    
    private void createPermissionsIfNotExist() {
        // Define basic permissions
        String[][] permissions = {
            {"USER_READ", "Permission to read user data", "USER", "READ"},
            {"USER_WRITE", "Permission to modify user data", "USER", "WRITE"},
            {"USER_DELETE", "Permission to delete users", "USER", "DELETE"},
            {"ROLE_MANAGE", "Permission to manage roles", "ROLE", "ADMIN"},
            {"AGENT_MANAGE", "Permission to manage agents", "AGENT", "ADMIN"},
            {"PROJECT_MANAGE", "Permission to manage projects", "PROJECT", "ADMIN"},
            {"TASK_MANAGE", "Permission to manage tasks", "TASK", "ADMIN"},
            {"KNOWLEDGE_MANAGE", "Permission to manage knowledge base", "KNOWLEDGE", "ADMIN"}
        };
        
        for (String[] p : permissions) {
            createPermissionIfNotExist(p[0], p[1], p[2], p[3]);
        }
    }
    
    private Permission createPermissionIfNotExist(String name, String description, String resourceType, String actionType) {
        Optional<Permission> existingPermission = permissionRepository.findByName(name);
        if (existingPermission.isPresent()) {
            return existingPermission.get();
        }
        
        log.info("Creating permission: {}", name);
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permission.setResourceType(resourceType);
        permission.setActionType(actionType);
        permission.setActive(true);
        
        return permissionRepository.save(permission);
    }
} 