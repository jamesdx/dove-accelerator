package com.doveaccelerator.auth.controller;

import com.doveaccelerator.auth.dto.UserDTO;
import com.doveaccelerator.auth.entity.User;
import com.doveaccelerator.auth.service.UserService;
import com.doveaccelerator.common.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<User>> getAllUsers(Pageable pageable) {
        return ApiResponse.success(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ApiResponse.success(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/roles/{roleName}")
    public ApiResponse<Void> assignRole(@PathVariable Long id, @PathVariable String roleName) {
        userService.assignRole(id, roleName);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ApiResponse<Void> removeRole(@PathVariable Long id, @PathVariable String roleName) {
        userService.removeRole(id, roleName);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return ApiResponse.success(null);
    }
}