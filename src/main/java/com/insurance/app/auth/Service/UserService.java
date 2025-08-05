package com.insurance.app.auth.Service;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.insurance.app.auth.Entity.Roles;
import com.insurance.app.auth.Entity.Users;
import com.insurance.app.auth.JWT_package.JwtTokenProvider;
import com.insurance.app.auth.Repository.UserRepository;
import com.insurance.app.auth.dto.ChangePasswordRequest;
import com.insurance.app.auth.dto.ForgotPasswordRequest;
import com.insurance.app.auth.dto.LoginRequest;
import com.insurance.app.auth.dto.LoginResponse;
import com.insurance.app.auth.dto.RegistrationRequest;
import com.insurance.app.auth.dto.UserProfileResponse;
import com.insurance.app.auth.inbuiltUtilyClasses.CustomUserDetails;

@Service
public class UserService { // customer
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse loginUser(LoginRequest req) throws AccessDeniedException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = customUserDetails.getUser();

        if (user.getRole() != Roles.CUSTOMER) {
            throw new AccessDeniedException("Access Denied for Customer area");
        }
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(user.getName(), user.getEmail(), user.getRole(), token);
    }

    public UserProfileResponse loadUserProfile(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserProfileResponse(user.getId(),user.getName(), user.getEmail(), user.getRole(),
                user.isActive(), user.getPanNumber());
    }

    public void changePassword(ChangePasswordRequest req) {
        Users user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password incorrect");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    public void forgotPassword(ForgotPasswordRequest req) {
        Users user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.getPanNumber().equals(req.getPanNumber())) {
            throw new IllegalArgumentException("Invalid PAN Number");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

	public String register(RegistrationRequest req) {
		
		Optional<Users> existingUser = userRepository.findByEmail(req.getEmail());
	    if (existingUser.isPresent()) {
	        return "User with the email already Exists";
	    }
		
		Users user = new Users();
	    user.setName(req.getName());
	    user.setEmail(req.getEmail());
	    user.setPassword(passwordEncoder.encode(req.getPassword()));
	    user.setPanNumber(req.getPanNumber());
	    user.setRole(req.getRole());
	    // Activate CUSTOMER by default; ADMIN inactive by default
	    user.setActive(req.getRole() == Roles.CUSTOMER);

	    userRepository.save(user);
	    return "User Registration Successful";
		
	}

	
	public boolean existsByEmail(String email) {	
		 Optional<Users> user=userRepository.findByEmail(email);
		 if(user.isPresent()) {
			 return true;
		 }
		
		return false;
	}
}

