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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LabReportServiceImpl implements ILabReportService{

    private final LabReportRepository labReportRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public LabReportServiceImpl(LabReportRepository labReportRepository, PatientRepository patientRepository, UserRepository userRepository) {
        this.labReportRepository = labReportRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }
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

        LabReport labReport = LabReportDto.mapToEntity(dto,patient);
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
    public LabReportDto getLabReportById(UUID id) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        return LabReportDto.mapToDTO(labReport);
    }

    @Override
    // Get all lab reports by patient ID
    public List<LabReportDto> getLabReportsByPatientId(UUID patientId) {
        List<LabReport> labReports = labReportRepository.findByPatientId(patientId);

        if (labReports.isEmpty()) {
            throw new ResourceNotFoundException("No lab reports found for patient ID: " + patientId);
        }

        return labReports.stream()
                .map(LabReportDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    // Update a lab report
    public LabReportDto updateLabReport(UUID id, LabReportDto dto) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        // Update fields
        labReport.setTestName(dto.getTestName());
        labReport.setResult(dto.getResult());
        labReport.setReportDate(dto.getReportDate());
        labReport.setConductedBy(dto.getConductedBy());

        return LabReportDto.mapToDTO(labReportRepository.save(labReport));
    }

    // Delete a lab report
    @Override
    public void deleteLabReport(UUID id) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        labReportRepository.delete(labReport);
    }

}
