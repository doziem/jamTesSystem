package com.doziem.jamTesSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPatientVisitDto {
    private String encounterId;
    private String patientId;
    private String mrn;
    private String patientName;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String visitType;
    private String status;
    private String departmentName;
    private LocalDateTime arrivalTime;
    private String triageNotes;
    private String assignedDoctorId;
    private String assignedDoctorName;
}
