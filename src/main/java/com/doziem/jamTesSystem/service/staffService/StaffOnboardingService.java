package com.doziem.jamTesSystem.service.staffService;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.constant.StaffStatus;
import com.doziem.jamTesSystem.constant.StaffType;
import com.doziem.jamTesSystem.exceptions.IllegalStateException;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.StaffMapper;
import com.doziem.jamTesSystem.model.Staff;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.StaffRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.StaffRequest;
import com.doziem.jamTesSystem.response.StaffOnboardingStatus;
import com.doziem.jamTesSystem.response.StaffResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
public class StaffOnboardingService {
    private static final EnumSet<Role> SELF_SELECTABLE_ROLES = EnumSet.of(
            Role.NURSE,
            Role.DOCTOR,
            Role.PHARMACIST,
            Role.STAFF,
            Role.LAB_SCIENTIST,
            Role.RADIOLOGIST,
            Role.RECEPTIONIST,
            Role.ACCOUNTANT,
            Role.HR
    );
    private static final EnumSet<Role> CLINICAL_ROLES = EnumSet.of(
            Role.NURSE,
            Role.DOCTOR,
            Role.PHARMACIST,
            Role.LAB_SCIENTIST,
            Role.RADIOLOGIST
    );

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public StaffOnboardingService(StaffRepository staffRepository, UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public StaffOnboardingStatus getOnboardingStatus(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return new StaffOnboardingStatus(staffRepository.existsByUser_Id(user.getId()));
    }

    @Transactional(readOnly = true)
    public StaffResponse getCurrentStaff(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Staff staff = staffRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found"));
        return StaffMapper.toResponse(staff);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByRole(Role role) {
        if (role == null) {
            return staffRepository.findAll().stream().map(StaffMapper::toResponse).toList();
        }
        return staffRepository.findByRole(role).stream().map(StaffMapper::toResponse).toList();
    }

    @Transactional
    public StaffResponse onboard(StaffRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (staffRepository.existsByUser_Id(user.getId())) {
            throw new IllegalStateException("Staff onboarding has already been completed");
        }
        if (!SELF_SELECTABLE_ROLES.contains(request.getRole())) {
            throw new IllegalArgumentException("Select a valid staff role");
        }
        if (CLINICAL_ROLES.contains(request.getRole()) && request.getStaffType() != StaffType.CLINICAL) {
            throw new IllegalArgumentException("Clinical roles must use the clinical staff type");
        }
        if (request.getStaffType() == StaffType.CLINICAL
                && (isBlank(request.getLicenseNumber()) || isBlank(request.getLicenseBody())
                || isBlank(request.getHighestQualification()))) {
            throw new IllegalArgumentException("Clinical staff must provide a license number, licensing body, and qualification");
        }

        Staff staff = Staff.builder()
                .staffNumber("STF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .firstName(request.getFirstName().trim())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName().trim())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .maritalStatus(request.getMaritalStatus())
                .nationality(request.getNationality())
                .stateOfOrigin(request.getStateOfOrigin())
                .phone(user.getPhone())
                .email(user.getEmail())
                .address(request.getAddress())
                .photoUrl(request.getPhotoUrl())
                .nationalId(request.getNationalId())
                .nextOfKinName(request.getNextOfKinName())
                .nextOfKinRelationship(request.getNextOfKinRelationship())
                .nextOfKinPhone(request.getNextOfKinPhone())
                .nextOfKinAddress(request.getNextOfKinAddress())
                .department(request.getDepartment().trim())
                .jobTitle(request.getJobTitle().trim())
                .role(request.getRole())
                .staffType(request.getStaffType())
                .employmentType(request.getEmploymentType())
                .dateOfEmployment(request.getDateOfEmployment())
                .probationEndDate(request.getProbationEndDate())
                .status(StaffStatus.ACTIVE)
                .licenseNumber(request.getLicenseNumber())
                .licenseBody(request.getLicenseBody())
                .licenseIssueDate(request.getLicenseIssueDate())
                .licenseExpiryDate(request.getLicenseExpiryDate())
                .specialty(request.getSpecialty())
                .highestQualification(request.getHighestQualification())
                .username(user.getEmail())
                .user(user)
                .createdBy(user.getEmail())
                .build();

        userRepository.save(user);
        return StaffMapper.toResponse(staffRepository.save(staff));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Unauthorized");
        }

        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .or(() -> userRepository.findByEmailOrPhone(authentication.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
