package com.doziem.jamTesSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDashboardDto {
    private String doctorId;
    private String doctorName;
    private String specialization;
    private int totalPatients;
    private int totalPrescriptions;
    private int pendingPrescriptions;
    private int pendingLabReports;
    private List<PrescriptionDto> recentPrescriptions;
    private List<LabReportDto> recentLabReports;
}
