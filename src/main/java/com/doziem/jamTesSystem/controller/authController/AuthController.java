package com.doziem.jamTesSystem.controller.authController;

import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.service.userService.IUserService;
import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.service.authService.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, IUserService userService, UserRepository userRepository, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authService = authService;
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
//        Optional<User> optionalUser = userRepository.findByEmailOrPhone(request.getEmailOrPhone());
//
//        System.out.println(optionalUser.isPresent());
//
//        if (optionalUser.isEmpty()) {
//            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials", null));
//        }
//
//
//        User user = optionalUser.get();
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
//        );
//
//        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
//
//        return ResponseEntity.ok(new AuthResponse("Login successful", token));
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials"));
        }
    }
}