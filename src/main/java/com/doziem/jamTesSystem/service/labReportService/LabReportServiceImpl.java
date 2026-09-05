package com.doziem.jamTesSystem.service.labReportService;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LabReportServiceImpl implements ILabReportService{

    private final LabReportRepository labReportRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;


    @Override
    // Create a new lab report
    public LabReportDto createLabReport(LabReportDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        User user = userRepository.findById(dto.getRequestedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.DOCTOR) {
            throw new UserNotAllowedException("Only a Doctor can request a test");
        }

        LabReport labReport = LabReportDto.mapToEntity(dto, patient, null);
        labReport.setPatient(patient);

        return LabReportDto.mapToDTO(labReportRepository.save(labReport));
    }

    @Override
    // Get all lab reports
    public List<LabReportDto> getAllLabReports() {
        return labReportRepository.findAll().stream()
                .map(LabReportDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    // Get a lab report by ID
    public LabReportDto getLabReportById(String id) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        return LabReportDto.mapToDTO(labReport);
    }

    @Override
    // Get all lab reports by patient ID
    public List<LabReportDto> getLabReportsByPatientId(String patientId) {
        List<LabReport> labReports = labReportRepository.findByPatientId(patientId);

        if (labReports.isEmpty()) {
            throw new ResourceNotFoundException("No lab reports found for patient ID: " + patientId);
        }

        return labReports.stream()
                .map(LabReportDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabReportDto> getLabReportsByRequestedBy(String requestedBy) {
        List<LabReport> labReports = labReportRepository.findByLabRequestRequestedById(requestedBy);
        if (labReports.isEmpty()) {
            throw new ResourceNotFoundException("No lab reports found for doctor: " + requestedBy);
        }
        return labReports.stream()
                .map(LabReportDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    // Update a lab report
    public LabReportDto updateLabReport(String id, LabReportDto dto) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        if (dto.getResult() != null && !dto.getResult().isBlank()) {
            if (dto.getConductedBy() == null) {
                throw new UserNotAllowedException("Only a Lab Scientist can submit the lab result");
            }

            User user = userRepository.findById(dto.getConductedBy())
                    .orElseThrow(() -> new ResourceNotFoundException("Lab scientist not found"));

            if (user.getRole() != Role.LAB_SCIENTIST) {
                throw new UserNotAllowedException("Only a Lab Scientist is allowed to send a lab result");
            }
        }

        labReport.setTestName(dto.getTestName() != null ? dto.getTestName() : labReport.getTestName());
        labReport.setResult(dto.getResult() != null ? dto.getResult() : labReport.getResult());
        labReport.setReportDate(dto.getReportDate() != null ? dto.getReportDate() : labReport.getReportDate());
        labReport.setConductedBy(dto.getConductedBy() != null ? dto.getConductedBy() : labReport.getConductedBy());

        return LabReportDto.mapToDTO(labReportRepository.save(labReport));
    }

    // Delete a lab report
    @Override
    public void deleteLabReport(String id) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        labReportRepository.delete(labReport);
    }

}
