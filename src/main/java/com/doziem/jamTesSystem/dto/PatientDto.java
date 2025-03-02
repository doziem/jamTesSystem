package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Address;
import com.doziem.jamTesSystem.model.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PatientDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private Address address;
    private boolean active;
    private List<LabReportDto> labReports = new ArrayList<>();
    private List<PrescriptionDto> prescriptions = new ArrayList<>();
    private List<BillingDto> billingRecords = new ArrayList<>();

    // Default Constructor
    public PatientDto() {}

    // Parameterized Constructor
    public PatientDto(UUID id, String firstName, String lastName, String email, String phone,
                      LocalDate dateOfBirth, String gender, Address address, boolean active,
                      List<LabReportDto> labReports, List<PrescriptionDto> prescriptions,
                      List<BillingDto> billingRecords) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.active = active;
        this.labReports = labReports;
        this.prescriptions = prescriptions;
        this.billingRecords = billingRecords;
    }

    // Getter and Setter Methods
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<LabReportDto> getLabReports() { return labReports; }
    public void setLabReports(List<LabReportDto> labReports) { this.labReports = labReports; }

    public List<PrescriptionDto> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<PrescriptionDto> prescriptions) { this.prescriptions = prescriptions; }

    public List<BillingDto> getBillingRecords() { return billingRecords; }
    public void setBillingRecords(List<BillingDto> billingRecords) { this.billingRecords = billingRecords; }

    // Convert Entity → DTO
    public static PatientDto mapToDTO(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getAddress(),
                patient.isActive(),
                patient.getLabReports().stream().map(LabReportDto::mapToDTO).toList(),
                patient.getPrescriptions().stream().map(PrescriptionDto::mapToDTO).toList(),
                patient.getBillingRecords().stream().map(BillingDto::mapToDTO).toList()
        );
    }

    public static Patient mapToEntity(PatientDto dto, Patient patient) {
        return new Patient(
                dto.getId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getGender(),
                dto.getDateOfBirth(),
                dto.getAddress(),
                dto.isActive(),
//                dto.getBillingRecords() != null ? dto.getBillingRecords().stream()
//                        .map(billingDto -> BillingDto.mapToEntity(billingDto, patient))
//                        .collect(Collectors.toList()) : new ArrayList<>(),
//                dto.getLabReports() != null ? dto.getLabReports().stream()
//                        .map(LabReportDto::mapToEntity)
//                        .collect(Collectors.toList()) : new ArrayList<>(),
//                dto.getPrescriptions() != null ? dto.getPrescriptions().stream()
//                        .map(prescriptionDto -> PrescriptionDto.mapToEntity(prescriptionDto, patient)) // Ensure patient is passed
//                        .collect(Collectors.toList()) : new ArrayList<>()
                dto.getBillingRecords() != null ? dto.getBillingRecords().stream()
                        .map(billingDto -> BillingDto.mapToEntity(billingDto, patient)) // Ensure patient is passed
                        .collect(Collectors.toList()) : new ArrayList<>(),
                dto.getLabReports() != null ? dto.getLabReports().stream()
                        .map(labReportDto -> LabReportDto.mapToEntity(labReportDto,patient)) // Adjusted here
                        .collect(Collectors.toList()) : new ArrayList<>(),
                dto.getPrescriptions() != null ? dto.getPrescriptions().stream()
                        .map(prescriptionDto -> PrescriptionDto.mapToEntity(prescriptionDto, patient)) // Ensure patient is passed
                        .collect(Collectors.toList()) : new ArrayList<>()
        );
    }
}
