package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.Patient;

import java.util.UUID;

public class LabReportDto {
    private UUID id;
    private UUID patientId;
    private String testName;
    private String result;
    private String reportDate;
    private String conductedBy;

    public LabReportDto() {}

    public LabReportDto(UUID id, UUID patientId, String testName, String result, String reportDate, String conductedBy) {
        this.id = id;
        this.patientId = patientId;
        this.testName = testName;
        this.result = result;
        this.reportDate = reportDate;
        this.conductedBy = conductedBy;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }

    public String getConductedBy() { return conductedBy; }
    public void setConductedBy(String conductedBy) { this.conductedBy = conductedBy; }

    // Convert DTO → Entity
    public static LabReport mapToEntity(LabReportDto dto) {
        return new LabReport(
                dto.getId(),
                dto.getPatientId(),
                dto.getTestName(),
                dto.getResult(),
                dto.getReportDate(),
                dto.getConductedBy()
        );
    }

    // Convert Entity → DTO
    public static LabReportDto mapToDTO(LabReport labReport) {
        return new LabReportDto(
                labReport.getId(),
                labReport.getPatient().getId(),
                labReport.getTestName(),
                labReport.getResult(),
                labReport.getReportDate(),
                labReport.getConductedBy()
        );
    }
}
