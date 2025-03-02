package com.doziem.jamTesSystem.controller.userController;

import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.userService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // Get all lab reports
    @GetMapping("/all")
    public ResponseEntity<List<ApiResponse>> getAllUsers() {
        try {
            return ResponseEntity.ok().body(Collections.singletonList(new ApiResponse("All user fetched", userService.getAllUsers())));
        }catch (Exception e){
            return ResponseEntity.ok().body(Collections.singletonList(new ApiResponse("", null)));

        }
    }
}

