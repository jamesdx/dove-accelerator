package com.doveaccelerator.auth.controller;

import com.doveaccelerator.auth.dto.LoginRequest;
import com.doveaccelerator.auth.dto.RegisterRequest;
import com.doveaccelerator.auth.dto.TokenResponse;
import com.doveaccelerator.auth.service.AuthService;
import com.doveaccelerator.common.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(@RequestParam String refreshToken) {
        return ApiResponse.success(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.replace("Bearer ", ""));
        return ApiResponse.success(null);
    }

    @GetMapping("/validate")
    public ApiResponse<Boolean> validateToken(@RequestParam String token) {
        return ApiResponse.success(authService.validateToken(token));
    }
}