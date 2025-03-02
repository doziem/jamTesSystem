package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.Patient;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public class LabReportDto {
    private UUID id;
    private UUID patientId;
    private UUID requestedBy;
    private String testName;
    private String result;
    private LocalDate reportDate;
    private LocalDate requestDate;
    private UUID conductedBy;

    public LabReportDto() {}

    public LabReportDto(UUID id, UUID patientId, UUID requestedBy, String testName, String result,
                        LocalDate reportDate, LocalDate requestDate, UUID conductedBy) {
        this.id = id;
        this.patientId = patientId;
        this.requestedBy = requestedBy;
        this.testName = testName;
        this.result = result;
        this.reportDate = reportDate;
        this.requestDate = requestDate;
        this.conductedBy = conductedBy;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public UUID getRequestedBy() { return requestedBy; }
    public void setRequestedBy(UUID requestedBy) { this.requestedBy = requestedBy; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public UUID getConductedBy() { return conductedBy; }
    public void setConductedBy(UUID conductedBy) { this.conductedBy = conductedBy; }

    public static LabReport mapToEntity(LabReportDto dto, Patient patient) {
        return new LabReport(
                dto.getId(),
                patient,
                dto.getRequestedBy(),
                dto.getTestName(),
                dto.getResult(),
                dto.getReportDate(),
                dto.getRequestDate(),
                dto.getConductedBy()
        );
    }

    public static LabReportDto mapToDTO(LabReport labReport) {
        return new LabReportDto(
                labReport.getId(),
                labReport.getPatient().getId(),
                labReport.getRequestedBy(),
                labReport.getTestName(),
                labReport.getResult(),
                labReport.getReportDate(),
                labReport.getRequestDate(),
                labReport.getConductedBy()
        );
    }
}