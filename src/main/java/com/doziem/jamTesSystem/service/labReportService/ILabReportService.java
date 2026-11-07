package com.doziem.jamTesSystem.service.labReportService;

import com.doziem.jamTesSystem.dto.LabReportDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ILabReportService {

    LabReportDto createLabReport(LabReportDto dto);

    LabReportDto createLabReport(LabReportDto dto, Authentication authentication);

    List<LabReportDto> getAllLabReports();

    List<LabReportDto> getAllLabReports(int page, int size);

    LabReportDto getLabReportById(String id);

    List<LabReportDto> getLabReportsByPatientId(String patientId);

    List<LabReportDto> getLabReportsByRequestedBy(String requestedBy);

    LabReportDto updateLabReport(String id, LabReportDto dto);

    LabReportDto updateLabReport(String id, LabReportDto dto, Authentication authentication);

    void deleteLabReport(String id);
}
