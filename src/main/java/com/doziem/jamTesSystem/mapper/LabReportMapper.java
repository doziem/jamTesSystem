package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.LabReportDto;
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
                .testName(dto.getTestName())
                .result(dto.getResult())
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
                .id(labReport.getId())
                .patientId(labReport.getPatient() != null ? labReport.getPatient().getId() : null)
                .requestedBy(labReport.getLabRequest().getRequestedBy().getId())
                .testName(labReport.getTestName())
                .result(labReport.getResult())
                .reportDate(labReport.getReportDate())
                .requestDate(labReport.getRequestDate())
                .conductedBy(labReport.getConductedBy())
                .labRequestId(labReport.getLabRequest().getId())
                .build();
    }
}
