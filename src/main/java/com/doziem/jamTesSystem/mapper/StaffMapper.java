package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.model.Staff;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.request.StaffRequest;
import com.doziem.jamTesSystem.request.UpdateStaffRequest;
import com.doziem.jamTesSystem.response.StaffResponse;
import org.springframework.stereotype.Component;

@Component
public class StaffMapper {

    public static Staff toEntity(StaffRequest r) {
        return Staff.builder()
                .firstName(r.getFirstName())
                .lastName(r.getLastName())
                .middleName(r.getMiddleName())
                .gender(r.getGender())
                .dateOfBirth(r.getDateOfBirth())
                .maritalStatus(r.getMaritalStatus())
                .nationality(r.getNationality())
                .stateOfOrigin(r.getStateOfOrigin())
                .phone(r.getPhone())
                .email(r.getEmail())
                .address(r.getAddress())
                .photoUrl(r.getPhotoUrl())
                .nationalId(r.getNationalId())
                .nextOfKinName(r.getNextOfKinName())
                .nextOfKinRelationship(r.getNextOfKinRelationship())
                .nextOfKinPhone(r.getNextOfKinPhone())
                .nextOfKinAddress(r.getNextOfKinAddress())
                .department(r.getDepartment())
                .jobTitle(r.getJobTitle())
                .role(r.getRole())
                .staffType(r.getStaffType())
                .employmentType(r.getEmploymentType())
                .dateOfEmployment(r.getDateOfEmployment())
                .probationEndDate(r.getProbationEndDate())
                .supervisor(r.getSupervisorId() == null ? null : Staff.builder()
                        .id(r.getSupervisorId())
                        .build())
                .status(r.getStatus())
                .licenseNumber(r.getLicenseNumber())
                .licenseBody(r.getLicenseBody())
                .licenseIssueDate(r.getLicenseIssueDate())
                .licenseExpiryDate(r.getLicenseExpiryDate())
                .specialty(r.getSpecialty())
                .highestQualification(r.getHighestQualification())
                .username(r.getUsername())
                .user(r.getUserId() == null ? null : User.builder()
                        .id(r.getUserId())
                        .build())
                .bankName(r.getBankName())
                .accountNumber(r.getAccountNumber())
                .accountName(r.getAccountName())
                .build();
    }

    public static StaffResponse toResponse(Staff s){

        String fullName = (s.getFirstName() + " "
                + (s.getMiddleName() == null || s.getMiddleName().isBlank() ? "" : s.getMiddleName() + " ")
                + s.getLastName()).trim();

        Staff sup = s.getSupervisor();
        String supervisorName = sup == null ? null : sup.getFirstName() + " " + sup.getLastName();

        return StaffResponse.builder()
                .id(s.getId())
                .userId(s.getUser() == null ? null : s.getUser().getId())
                .staffNumber(s.getStaffNumber())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .middleName(s.getMiddleName())
                .fullName(fullName)
                .gender(s.getGender())
                .dateOfBirth(s.getDateOfBirth())
                .maritalStatus(s.getMaritalStatus())
                .nationality(s.getNationality())
                .stateOfOrigin(s.getStateOfOrigin())
                .phone(s.getPhone())
                .email(s.getEmail())
                .address(s.getAddress())
                .photoUrl(s.getPhotoUrl())
                .nextOfKinName(s.getNextOfKinName())
                .nextOfKinRelationship(s.getNextOfKinRelationship())
                .nextOfKinPhone(s.getNextOfKinPhone())
                .department(s.getDepartment())
                .jobTitle(s.getJobTitle())
                .role(s.getRole())
                .staffType(s.getStaffType())
                .employmentType(s.getEmploymentType())
                .dateOfEmployment(s.getDateOfEmployment())
                .probationEndDate(s.getProbationEndDate())
                .supervisorId(sup == null ? null : sup.getId())
                .supervisorName(supervisorName)
                .status(s.getStatus())

                .licenseNumber(s.getLicenseNumber())
                .licenseBody(s.getLicenseBody())
                .licenseIssueDate(s.getLicenseIssueDate())
                .licenseExpiryDate(s.getLicenseExpiryDate())
                .specialty(s.getSpecialty())
                .highestQualification(s.getHighestQualification())
                .lastLoginAt(s.getLastLoginAt())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())

                .build();



    }
//    update staff entity with staff request
    public static Staff updateEntity(Staff s, UpdateStaffRequest r) {
        s.setFirstName(r.getFirstName());
        s.setLastName(r.getLastName());
        s.setMiddleName(r.getMiddleName());
        s.setGender(r.getGender());
        s.setDateOfBirth(r.getDateOfBirth());
        s.setMaritalStatus(r.getMaritalStatus());
        s.setNationality(r.getNationality());
        s.setStateOfOrigin(r.getStateOfOrigin());
        s.setPhone(r.getPhone());
        s.setEmail(r.getEmail());
        s.setAddress(r.getAddress());
        s.setPhotoUrl(r.getPhotoUrl());
        s.setNationalId(r.getNationalId());
        s.setNextOfKinName(r.getNextOfKinName());
        s.setNextOfKinRelationship(r.getNextOfKinRelationship());
        s.setNextOfKinPhone(r.getNextOfKinPhone());
        s.setNextOfKinAddress(r.getNextOfKinAddress());
        s.setDepartment(r.getDepartment());
        s.setJobTitle(r.getJobTitle());
        s.setRole(r.getRole());
        s.setStaffType(r.getStaffType());
        s.setEmploymentType(r.getEmploymentType());
        s.setDateOfEmployment(r.getDateOfEmployment());
        s.setProbationEndDate(r.getProbationEndDate());

        if (r.getSupervisorId() != null) {
            Staff supervisor = new Staff();
            supervisor.setId(r.getSupervisorId());
            s.setSupervisor(supervisor);
        } else {
            s.setSupervisor(null);
        }
        s.setStatus(r.getStatus());

        return s;
    }
}
