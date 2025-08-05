package com.insurance.app.auth.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.app.auth.Entity.Roles;
import com.insurance.app.auth.Service.AdminService;
import com.insurance.app.auth.dto.UserProfileResponse;
import com.insurance.app.auth.inbuiltUtilyClasses.CustomUserDetails;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(adminService.loadUserProfile(userDetails.getUsername()));
    }

    @GetMapping("/pending-admins")
    public ResponseEntity<List<UserProfileResponse>> getPendingAdmins() {
        return ResponseEntity.ok(adminService.listInactiveUsersByRole(Roles.ADMIN));
    }

    @GetMapping("/pending-customers")
    public ResponseEntity<List<UserProfileResponse>> getPendingCustomers() {
        return ResponseEntity.ok(adminService.listInactiveUsersByRole(Roles.CUSTOMER));
    }

    @PostMapping("/activate-admin/{id}")
    public ResponseEntity<String> activateAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.activateAdmin(id));
    }

    @PostMapping("/deactivate-user/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deactivateUser(id));
    }

    @GetMapping("/find-user")
    public ResponseEntity<UserProfileResponse> findUser(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long id) {
        return ResponseEntity.ok(adminService.findUserByEmailOrId(email, id));
    }
}
