package com.doziem.jamTesSystem.model;

import com.doziem.jamTesSystem.constant.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "staff_number", nullable = false, updatable = false, length = 30)
    private String staffNumber;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "middle_name", length = 80)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private ZonedDateTime dateOfBirth;

    @Column(name = "marital_status", length = 30)
    private String maritalStatus;

    @Column(length = 60)
    private String nationality;

    @Column(name = "state_of_origin", length = 60)
    private String stateOfOrigin;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "national_id", length = 30)
    private String nationalId;

    // ---------- Next of kin / emergency contact ----------
    @Column(name = "next_of_kin_name", length = 150)
    private String nextOfKinName;

    @Column(name = "next_of_kin_relationship", length = 50)
    private String nextOfKinRelationship;

    @Column(name = "next_of_kin_phone", length = 20)
    private String nextOfKinPhone;

    @Column(name = "next_of_kin_address", length = 255)
    private String nextOfKinAddress;

    // ---------- Employment details ----------
    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type", nullable = false, length = 30)
    private StaffType staffType;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 30)
    private EmploymentType employmentType;

    @Column(name = "date_of_employment", nullable = false)
    private ZonedDateTime dateOfEmployment;

    @Column(name = "probation_end_date")
    private ZonedDateTime probationEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Staff supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StaffStatus status = StaffStatus.ACTIVE;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "license_body", length = 50)
    private String licenseBody;

    @Column(name = "license_issue_date")
    private ZonedDateTime licenseIssueDate;

    @Column(name = "license_expiry_date")
    private ZonedDateTime licenseExpiryDate;

    @Column(length = 100)
    private String specialty;

    @Column(name = "highest_qualification", length = 150)
    private String highestQualification;

    @Column(nullable = false, length = 60)
    private String username;

    // ---------- Login account (email, name, password, isVerified live on User) ----------
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

//    ------ Bank Details
    private String bankName;
    private String accountNumber;
    private String accountName;


    @Column(name = "last_login_at")
    private ZonedDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    void onCreate() {
        ZonedDateTime now = ZonedDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }
}
