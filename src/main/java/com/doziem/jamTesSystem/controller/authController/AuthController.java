package com.doziem.jamTesSystem.controller.authController;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.authService.IAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;
    private final String frontendLoginUrl;

    public AuthController(IAuthService authService,
                          @Value("${app.frontend.login-url:http://localhost:3000/login}") String frontendLoginUrl) {
        this.authService = authService;
        this.frontendLoginUrl = frontendLoginUrl;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage() != null ? e.getMessage() : "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createUser(@RequestBody UserDto userDto) {
        try {
            AuthResponse createdUser = authService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(e.getMessage() != null ? e.getMessage() : "Error creating user"));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String email, @RequestParam(required = false) String token) {
        try {
            return ResponseEntity.ok(authService.verifyEmail(email, token));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmailAndRedirect(@RequestParam String email, @RequestParam(required = false) String token) {
        try {
            authService.verifyEmail(email, token);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(buildLoginRedirectUri("Email verified successfully. You can now log in."))
                    .build();
        } catch (ResourceNotFoundException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(buildLoginRedirectUri(ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        try {
            return ResponseEntity.ok(authService.getCurrentUser(authentication));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private URI buildLoginRedirectUri(String message) {
        return UriComponentsBuilder.fromUriString(frontendLoginUrl)
                .queryParam("message", message)
                .build(true)
                .toUri();
    }
}