package com.doziem.jamTesSystem.controller.authController;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.service.userService.IUserService;
import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.service.authService.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final IUserService userService;

    private final AuthService authService;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, IUserService userService, UserRepository userRepository, IUserService userService1, AuthService authService) {
        this.userService = userService1;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDto userDto) {
        try {
            UserDto createdUser = userService.createUser(userDto);
            return  ResponseEntity.status(HttpStatus.CREATED).body( new ApiResponse("USer created Successfully",createdUser));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error creating User",null));
        }

    }
}