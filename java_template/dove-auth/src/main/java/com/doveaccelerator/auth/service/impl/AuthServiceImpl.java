package com.doveaccelerator.auth.service.impl;

import com.doveaccelerator.auth.config.JwtConfig;
import com.doveaccelerator.auth.dto.LoginRequest;
import com.doveaccelerator.auth.dto.RegisterRequest;
import com.doveaccelerator.auth.dto.TokenResponse;
import com.doveaccelerator.auth.entity.User;
import com.doveaccelerator.auth.service.AuthService;
import com.doveaccelerator.auth.service.UserService;
import com.doveaccelerator.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userService.getByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return generateTokens(user);
    }

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        user = userService.createUser(user);
        return generateTokens(user);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtConfig.validateToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }

        String username = jwtConfig.extractUsername(refreshToken);
        User user = userService.getByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        return generateTokens(user);
    }

    @Override
    public void logout(String token) {
        // Implementation for token blacklisting or invalidation
    }

    @Override
    public boolean validateToken(String token) {
        return jwtConfig.validateToken(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtConfig.extractUsername(token);
    }

    private TokenResponse generateTokens(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        String accessToken = jwtConfig.generateToken(user.getUsername(), claims);
        String refreshToken = jwtConfig.generateRefreshToken(user.getUsername());

        return new TokenResponse(accessToken, refreshToken);
    }
}