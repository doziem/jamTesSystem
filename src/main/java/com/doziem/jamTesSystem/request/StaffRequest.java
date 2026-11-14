package com.doziem.jamTesSystem.request;

import com.doziem.jamTesSystem.constant.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StaffRequest {

        // ---------- Personal ----------
        @NotBlank @Size(max = 80)
       private String firstName;
        @Size(max = 80)
        private String middleName;
        @NotBlank
        @Size(max = 80)
        private String lastName;
        @NotNull Gender gender;
        @NotNull
        @Past
        private ZonedDateTime dateOfBirth;
        @Size(max = 30)
        private String maritalStatus;
        @Size(max = 60)
        private String nationality;
        @Size(max = 60)
        private String stateOfOrigin;
        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "must be a valid phone number")
        private String phone;
        @NotBlank @Email @Size(max = 150)
        private String email;
        @Size(max = 255)
        private String address;
        @Size(max = 500)
        private String photoUrl;
        @Size(max = 30)
        private String nationalId;

 private StaffStatus status;

 private String username;

 private String userId;


 // ---------- Next of kin ----------
        @Size(max = 150)
        private String nextOfKinName;
        @Size(max = 50)
        private String nextOfKinRelationship;
        @Size(max = 20)
        private String nextOfKinPhone;
        @Size(max = 255)
        private String nextOfKinAddress;

        // ---------- Employment ----------
        @NotBlank @Size(max = 100)
        private String department;
        @NotBlank @Size(max = 100)
        private String jobTitle;
        @NotNull
        private Role role;
        @NotNull
        private StaffType staffType;
        @NotNull
        private EmploymentType employmentType;
        @NotNull
        @PastOrPresent
        private ZonedDateTime dateOfEmployment;
        private ZonedDateTime probationEndDate;
        private String supervisorId;

        // ---------- Credentials (required for clinical staff, validated in the service) ----------
        @Size(max = 50)
        private String licenseNumber;
        @Size(max = 50)
        private String licenseBody;
        private ZonedDateTime licenseIssueDate;
        private ZonedDateTime licenseExpiryDate;
        @Size(max = 100)
        private String specialty;
        @Size(max = 150)
        private String highestQualification;

//        -------Bank Detail
        private String bankName;
        private String accountNumber;
        private String accountName;
}
