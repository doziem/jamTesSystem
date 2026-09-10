package com.doziem.jamTesSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterDto {
    private String id;
    private String patientId;
    private String visitType;
    private String status;
    private LocalDateTime arrivalTime;
    private String departmentName;
    private String assignedDoctorId;
    private String triageNotes;
}
