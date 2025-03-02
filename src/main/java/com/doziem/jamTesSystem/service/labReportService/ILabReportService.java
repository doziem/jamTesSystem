package com.doziem.jamTesSystem.service.labReportService;

import com.doziem.jamTesSystem.dto.LabReportDto;

import java.util.List;
import java.util.UUID;

public interface ILabReportService {

    LabReportDto createLabReport(LabReportDto dto);

    List<LabReportDto> getAllLabReports();

    LabReportDto getLabReportById(UUID id);

    List<LabReportDto> getLabReportsByPatientId(UUID patientId);

    LabReportDto updateLabReport(UUID id, LabReportDto dto);

    void deleteLabReport(UUID id);
}
