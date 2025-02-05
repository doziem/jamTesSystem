package com.doziem.jamTesSystem.controller.userController;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.userService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
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

