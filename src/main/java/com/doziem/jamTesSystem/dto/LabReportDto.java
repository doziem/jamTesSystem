package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.LabRequest;
import com.doziem.jamTesSystem.model.Patient;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LabReportDto {
    private String id;
    private String patientId;
    private String requestedBy;
    private String testName;
    private String result;
    private LocalDate reportDate;
    private LocalDate requestDate;
    private String conductedBy;
    private String labRequestId;
    private List<String> testNames = new ArrayList<>();


    public LabReportDto(String id, String patientId, String requestedBy, String testName, String result,
                        LocalDate reportDate, LocalDate requestDate, String conductedBy, String labRequestId) {
        this.id = id;
        this.patientId = patientId;
        this.requestedBy = requestedBy;
        this.testName = testName;
        this.result = result;
        this.reportDate = reportDate;
        this.requestDate = requestDate;
        this.conductedBy = conductedBy;
        this.labRequestId = labRequestId;
    }

    public static LabReport mapToEntity(LabReportDto dto, Patient patient, LabRequest labRequest) {
        return new LabReport(
                dto.getId(),
                patient,
                labRequest,
                dto.getTestName(),
                dto.getResult(),
                dto.getReportDate(),
                dto.getRequestDate(),
                dto.getConductedBy()
        );
    }

    public static LabReportDto mapToDTO(LabReport labReport) {
        if (labReport.getLabRequest() == null || labReport.getLabRequest().getRequestedBy() == null) {
            throw new IllegalStateException("Lab request and requesting doctor must be present for lab report mapping");
        }
        return new LabReportDto(
                labReport.getId(),
                labReport.getPatient().getId(),
                labReport.getLabRequest().getRequestedBy().getId(),
                labReport.getTestName(),
                labReport.getResult(),
                labReport.getReportDate(),
                labReport.getRequestDate(),
                labReport.getConductedBy(),
                labReport.getLabRequest().getId()
        );
    }
}