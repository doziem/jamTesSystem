package com.doziem.jamTesSystem.controller.staffController;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.request.StaffRequest;
import com.doziem.jamTesSystem.response.StaffOnboardingStatus;
import com.doziem.jamTesSystem.response.StaffResponse;
import com.doziem.jamTesSystem.service.staffService.StaffOnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffOnboardingService staffOnboardingService;

    public StaffController(StaffOnboardingService staffOnboardingService) {
        this.staffOnboardingService = staffOnboardingService;
    }

    @GetMapping("/onboarding-status")
    public ResponseEntity<StaffOnboardingStatus> getOnboardingStatus(Authentication authentication) {
        return ResponseEntity.ok(staffOnboardingService.getOnboardingStatus(authentication));
    }

    @PostMapping("/onboard")
    public ResponseEntity<StaffResponse> onboard(@Valid @RequestBody StaffRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffOnboardingService.onboard(request, authentication));
    }

    @GetMapping("/me")
    public ResponseEntity<StaffResponse> getCurrentStaff(Authentication authentication) {
        return ResponseEntity.ok(staffOnboardingService.getCurrentStaff(authentication));
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getStaff(@RequestParam(required = false) Role role) {
        return ResponseEntity.ok(staffOnboardingService.getStaffByRole(role));
    }
}
