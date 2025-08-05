package com.insurance.app.auth.Controller;

import java.nio.file.AccessDeniedException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.app.auth.Entity.Roles;
import com.insurance.app.auth.Service.AdminService;
import com.insurance.app.auth.Service.UserService;
import com.insurance.app.auth.dto.ChangePasswordRequest;
import com.insurance.app.auth.dto.ForgotPasswordRequest;
import com.insurance.app.auth.dto.LoginRequest;
import com.insurance.app.auth.dto.LoginResponse;
import com.insurance.app.auth.dto.RegistrationRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;

    public AuthController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest req) {
        // Route to userService or adminService based on role
    	String status="";
        if (req.getRole() == Roles.CUSTOMER) {
            status = userService.register(req);  // You’d implement register in UserService to save user active by default
        } else {
        	status = adminService.register(req);  // Same for admin, inactive by default
        }
        return ResponseEntity.ok(status);
    }

    @PostMapping("/login/customer")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest req) throws AccessDeniedException {
        return ResponseEntity.ok(userService.loginUser(req));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest req) throws AccessDeniedException {
        return ResponseEntity.ok(adminService.loginAdmin(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        // Try both services since it can be either user or admin
        try {
            userService.forgotPassword(req);
        } catch (UsernameNotFoundException e) {
            adminService.forgotPassword(req);
        }
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest req) {
        // You can add logic to decide which service handles this or merge logic in common service.
        if (userService.existsByEmail(req.getEmail())) {
            userService.changePassword(req);
        } else {
            adminService.changePassword(req);
        }
        return ResponseEntity.ok("Password changed successfully");
    }
}

