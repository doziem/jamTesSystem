package com.doziem.jamTesSystem.controller.userController;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.request.ProfileRequest;
import com.doziem.jamTesSystem.response.ProfileResponse;
import com.doziem.jamTesSystem.service.userService.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody UserDto userDto, Authentication authentication) {
        try {
            return userService.updateUser(id, userDto, userDto.getPassword(), authentication)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (UserNotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable String id, Authentication authentication) {
        try {
            return userService.deactivateUser(id, authentication)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (UserNotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id, Authentication authentication) {
        try {
            return userService.deleteUser(id, authentication)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (UserNotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/{userId}/profile")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProfileResponse> createProfile(@PathVariable String userId, @RequestBody ProfileRequest request) {
        try {
            return ResponseEntity.ok(userService.createProfileResponse(userId, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/{userId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String userId, Authentication authentication) {
        try {
            return ResponseEntity.ok(userService.getProfileResponse(userId, authentication));
        } catch (UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
