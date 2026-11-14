package com.doziem.jamTesSystem.service.staffService;

import com.doziem.jamTesSystem.constant.EmploymentType;
import com.doziem.jamTesSystem.constant.Gender;
import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.constant.StaffType;
import com.doziem.jamTesSystem.model.Staff;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.StaffRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.StaffRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffOnboardingServiceTest {
    @Mock
    private StaffRepository staffRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    private StaffOnboardingService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new StaffOnboardingService(staffRepository, userRepository);
        user = User.builder()
                .id("user-1")
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("+2348012345678")
                .build();
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void statusIsIncompleteUntilStaffProfileExists() {
        when(staffRepository.existsByUser_Id(user.getId())).thenReturn(false);

        assertFalse(service.getOnboardingStatus(authentication).onboarded());
    }

    @Test
    void onboardingCreatesStaffProfile() {
        when(staffRepository.existsByUser_Id(user.getId())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.onboard(validRequest(), authentication);

        assertEquals(Role.DOCTOR, response.getRole());
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void onboardingDoesNotAllowPrivilegedRoles() {
        when(staffRepository.existsByUser_Id(user.getId())).thenReturn(false);
        StaffRequest request = validRequest();
        request.setRole(Role.ADMIN);

        assertThrows(IllegalArgumentException.class, () -> service.onboard(request, authentication));
        verify(staffRepository, never()).save(any(Staff.class));
    }

    private StaffRequest validRequest() {
        return StaffRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .gender(Gender.FEMALE)
                .dateOfBirth(ZonedDateTime.now().minusYears(25))
                .email(user.getEmail())
                .phone(user.getPhone())
                .department("Outpatient")
                .jobTitle("Physician")
                .role(Role.DOCTOR)
                .staffType(StaffType.CLINICAL)
                .employmentType(EmploymentType.FULL_TIME)
                .dateOfEmployment(ZonedDateTime.now().minusMonths(2))
                .licenseNumber("LIC-123")
                .licenseBody("Medical Council")
                .highestQualification("MBBS")
                .build();
    }
}
