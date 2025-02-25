package com.doveaccelerator.auth.service;

import com.doveaccelerator.auth.dto.LoginRequest;
import com.doveaccelerator.auth.dto.RegisterRequest;
import com.doveaccelerator.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    
    TokenResponse register(RegisterRequest request);
    
    TokenResponse refreshToken(String refreshToken);
    
    void logout(String token);
    
    boolean validateToken(String token);
    
    String getUsernameFromToken(String token);
}