package com.doziem.jamTesSystem.service.labReportService;

import com.doziem.jamTesSystem.dto.LabReportDto;

import java.util.List;

public interface ILabReportService {

    LabReportDto createLabReport(LabReportDto dto);

    List<LabReportDto> getAllLabReports();

    List<LabReportDto> getAllLabReports(int page, int size);

    LabReportDto getLabReportById(String id);

    List<LabReportDto> getLabReportsByPatientId(String patientId);

    List<LabReportDto> getLabReportsByRequestedBy(String requestedBy);

    LabReportDto updateLabReport(String id, LabReportDto dto);

    void deleteLabReport(String id);
}
