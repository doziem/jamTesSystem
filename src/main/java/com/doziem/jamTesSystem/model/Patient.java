package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

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

    // Relationships
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<LabReport> labReports;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Billing> billingRecords;

    public Patient() {
    }

    public Patient(UUID id, String firstName, String lastName, String email, String phone, String gender, LocalDate dateOfBirth, Address address, boolean active, List<Billing> billingRecords, List<LabReport> labReports, List<Prescription> prescriptions) {
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Billing> getBillingRecords() {
        return billingRecords;
    }

    public void setBillingRecords(List<Billing> billingRecords) {
        this.billingRecords = billingRecords;
    }

    public List<LabReport> getLabReports() {
        return labReports;
    }

    public void setLabReports(List<LabReport> labReports) {
        this.labReports = labReports;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

