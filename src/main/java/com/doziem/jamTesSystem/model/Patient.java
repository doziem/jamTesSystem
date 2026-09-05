package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<LabReport> labReports;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Billing> billingRecords;

    public Patient() {
    }

    public Patient(String id, String firstName, String lastName, String email, String phone, String gender,
                   LocalDate dateOfBirth, Address address, boolean active,
                   List<Billing> billingRecords, List<LabReport> labReports, List<Prescription> prescriptions) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.active = active;
        this.billingRecords = billingRecords;
        this.labReports = labReports;
        this.prescriptions = prescriptions;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}