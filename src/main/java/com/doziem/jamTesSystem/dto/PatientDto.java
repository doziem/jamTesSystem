package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Address;
import com.doziem.jamTesSystem.model.Patient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PatientDto {

    private String id;
    private String mrn;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private Address address;
    private boolean active;
    private List<EncounterDto> encounters = new ArrayList<>();
    private List<LabReportDto> labReports = new ArrayList<>();
    private List<PrescriptionDto> prescriptions = new ArrayList<>();
    private List<BillingDto> billingRecords = new ArrayList<>();

    // Default Constructor
    public PatientDto() {}

    // Parameterized Constructor
    public PatientDto(String id, String mrn, String firstName, String lastName, String email, String phone,
                      LocalDate dateOfBirth, String gender, Address address, boolean active,
                      List<EncounterDto> encounters,
                      List<LabReportDto> labReports, List<PrescriptionDto> prescriptions,
                      List<BillingDto> billingRecords) {
        this.id = id;
        this.mrn = mrn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.active = active;
        this.encounters = encounters;
        this.labReports = labReports;
        this.prescriptions = prescriptions;
        this.billingRecords = billingRecords;
    }


}