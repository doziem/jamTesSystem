package com.doziem.jamTesSystem.response;

import com.doziem.jamTesSystem.constant.*;
import lombok.*;

import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StaffResponse {
    String id;
    String userId;
    String staffNumber;

    String firstName;
    String middleName;
    String lastName;
    String fullName;
    Gender gender;
    ZonedDateTime dateOfBirth;
    String maritalStatus;
    String nationality;
    String stateOfOrigin;
    String phone;
    String email;
    String address;
    String photoUrl;

    String nextOfKinName;
    String nextOfKinRelationship;
    String nextOfKinPhone;

    String department;
    String jobTitle;
    Role role;
    StaffType staffType;
    EmploymentType employmentType;
    ZonedDateTime dateOfEmployment;
    ZonedDateTime probationEndDate;
    String supervisorId;
    String supervisorName;
    StaffStatus status;

    String licenseNumber;
    String licenseBody;
    ZonedDateTime licenseIssueDate;
    ZonedDateTime licenseExpiryDate;
    String specialty;
    String highestQualification;
    ZonedDateTime lastLoginAt;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
}
