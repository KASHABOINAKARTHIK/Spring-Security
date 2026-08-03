package com.insurance.app.auth.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.app.auth.Service.UserService;
import com.insurance.app.auth.dto.UserProfileResponse;
//import com.insurance.app.auth.inbuiltUtilyClasses.CustomUserDetails;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@CrossOrigin(
	    origins = "http://localhost:4200",
	    allowCredentials = "true"
	)
public class CustomerController {

    private final UserService userService;

    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.loadUserProfile(email));
    }
    
   
}