package com.doveaccelerator.auth.aspect;

import com.doveaccelerator.auth.annotation.RequirePermission;
import com.doveaccelerator.auth.entity.Permission;
import com.doveaccelerator.auth.entity.Role;
import com.doveaccelerator.auth.entity.User;
import com.doveaccelerator.auth.repository.UserRepository;
import com.doveaccelerator.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final UserRepository userRepository;

    @Before("@annotation(com.doveaccelerator.auth.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        String requiredPermission = annotation.value();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }
        
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            throw new BusinessException("User not found");
        }
        
        User user = userOpt.get();
        Set<String> permissions = user.getRoles().stream()
                .map(Role::getPermissions)
                .flatMap(Set::stream)
                .map(Permission::getName)
                .collect(Collectors.toSet());
        
        if (!permissions.contains(requiredPermission)) {
            log.warn("User {} does not have required permission: {}", username, requiredPermission);
            throw new BusinessException("Insufficient permissions");
        }
        
        log.debug("Permission check passed for user {} with permission {}", username, requiredPermission);
    }
} 