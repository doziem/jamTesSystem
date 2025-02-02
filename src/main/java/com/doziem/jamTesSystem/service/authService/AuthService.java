package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest request) {
        // Debug: Log the login attempt
        System.out.println("Login attempt with: " + request.getEmailOrPhone());

        // Fetch user by email or phone
        Optional<User> optionalUser = userRepository.findByEmailOrPhone(request.getEmailOrPhone());
        // Debug: Check if user exists
        System.out.println("User found: " + optionalUser.isPresent());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = optionalUser.get();

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        // Generate JWT token
        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

        // Return successful response
        return new AuthResponse("Login successful",user.getName(),user.getEmail(),user.getPhone(),user.getRole(),user.isActive(), token);
    }
}
