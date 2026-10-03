package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.dto.LabTestResultDto;
import com.doziem.jamTesSystem.model.LabReportEntry;
import com.doziem.jamTesSystem.model.LabRequest;
import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class LabReportMapper {
    public LabReport toEntity(LabReportDto dto, Patient patient, LabRequest labRequest) {
        return LabReport.builder()
                .id(dto.getId())
                .patient(patient)
                .labRequest(labRequest)
                .reportDate(dto.getReportDate())
                .requestDate(dto.getRequestDate())
                .conductedBy(dto.getConductedBy())
                .build();
    }

    public LabReportDto toDto(LabReport labReport) {
        if (labReport.getLabRequest() == null || labReport.getLabRequest().getRequestedBy() == null) {
            throw new IllegalStateException("Lab request and requesting doctor must be present for lab report mapping");
        }
        return LabReportDto.builder()
                .id(labReport.getId() != null ? labReport.getId().toString() : null)
                .patientId(labReport.getPatient() != null && labReport.getPatient().getId() != null ? labReport.getPatient().getId().toString() : null)
                .requestedBy(labReport.getLabRequest().getRequestedBy().getId() != null ? labReport.getLabRequest().getRequestedBy().getId().toString() : null)
                .reportDate(labReport.getReportDate())
                .requestDate(labReport.getRequestDate())
                .conductedBy(labReport.getConductedBy())
                .labRequestId(labReport.getLabRequest().getId() != null ? labReport.getLabRequest().getId().toString() : null)
                .testResults(
                        labReport.getTestResults().stream()
                                .map(this::toLabTestResultDto)
                                .toList()
                )
                .build();
    }

    private LabTestResultDto toLabTestResultDto(LabReportEntry entry) {
        return LabTestResultDto.builder()
                .id(entry.getId())
                .testName(entry.getTestName())
                .result(entry.getResult())
                .build();
    }
}
