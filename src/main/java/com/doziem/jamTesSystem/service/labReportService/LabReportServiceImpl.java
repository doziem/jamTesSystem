package com.doziem.jamTesSystem.service.labReportService;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.dto.LabTestResultDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.mapper.LabReportMapper;
import com.doziem.jamTesSystem.model.LabReportEntry;
import com.doziem.jamTesSystem.model.LabRequest;
import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.LabRequestRepository;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class LabReportServiceImpl implements ILabReportService{

    private final LabReportRepository labReportRepository;
    private final LabRequestRepository labRequestRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final LabReportMapper labReportMapper;


    @Override
    public LabReportDto createLabReport(LabReportDto dto, Authentication authentication) {
        dto.setRequestedBy(authenticatedUser(authentication, Role.DOCTOR).getId());
        return createLabReport(dto);
    }

    @Override
    public LabReportDto createLabReport(LabReportDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        User user = userRepository.findById(dto.getRequestedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.DOCTOR) {
            throw new UserNotAllowedException("Only a Doctor can request a test");
        }

        validateTestResults(dto.getTestResults(), false);
        LabRequest labRequest = labRequestRepository.save(LabRequest.builder()
                .patient(patient)
                .requestedBy(user)
                .requestDate(LocalDate.now())
                .build());

        LabReport labReport = labReportMapper.toEntity(dto, patient, labRequest);
        labReport.setId(null);
        labReport.setRequestDate(labRequest.getRequestDate());
        labReport.setReportDate(null);
        labReport.setConductedBy(null);
        labReport.setPatient(patient);
        labReport.setTestResults(mapTestResults(dto.getTestResults(), labReport));

        return labReportMapper.toDto(labReportRepository.save(labReport));
    }

    @Override
    // Get all lab reports
    public List<LabReportDto> getAllLabReports() {
        return getAllLabReports(0, 10);
    }

    @Override
    public List<LabReportDto> getAllLabReports(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        return labReportRepository.findAll().stream()
                .skip((long) page * size)
                .limit(size)
                .map(labReportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    // Get a lab report by ID
    public LabReportDto getLabReportById(String id) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        return labReportMapper.toDto(labReport);
    }

    @Override
    // Get all lab reports by patient ID
    public List<LabReportDto> getLabReportsByPatientId(String patientId) {
        List<LabReport> labReports = labReportRepository.findByPatientId(patientId);

        if (labReports.isEmpty()) {
            throw new ResourceNotFoundException("No lab reports found for patient ID: " + patientId);
        }

        return labReports.stream()
                .map(labReportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabReportDto> getLabReportsByRequestedBy(String requestedBy) {
        List<LabReport> labReports = labReportRepository.findByLabRequestRequestedById(requestedBy);
        if (labReports.isEmpty()) {
            throw new ResourceNotFoundException("No lab reports found for doctor: " + requestedBy);
        }
        return labReports.stream()
                .map(labReportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabReportDto updateLabReport(String id, LabReportDto dto, Authentication authentication) {
        dto.setConductedBy(authenticatedUser(authentication, Role.LAB_SCIENTIST).getId());
        dto.setReportDate(LocalDate.now());
        validateTestResults(dto.getTestResults(), true);
        return updateLabReport(id, dto);
    }

    @Override
    public LabReportDto updateLabReport(String id, LabReportDto dto) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        if (dto.getConductedBy() != null && !dto.getConductedBy().isBlank()) {
            validateLabScientist(dto.getConductedBy());
        }

        if (dto.getTestResults() != null && !dto.getTestResults().isEmpty()) {
            if (dto.getConductedBy() == null || dto.getConductedBy().isBlank()) {
                throw new UserNotAllowedException("Only a Lab Scientist can submit the lab result");
            }
            validateLabScientist(dto.getConductedBy());
            validateTestResults(dto.getTestResults(), true);
            List<String> requestedTests = labReport.getTestResults().stream().map(LabReportEntry::getTestName).sorted().toList();
            List<String> submittedTests = dto.getTestResults().stream().map(item -> item.getTestName().trim()).sorted().toList();
            if (!requestedTests.equals(submittedTests)) {
                throw new IllegalArgumentException("Results must match the requested tests");
            }
            labReport.getTestResults().clear();
            labReport.getTestResults().addAll(mapTestResults(dto.getTestResults(), labReport));
        }

        labReport.setReportDate(dto.getReportDate() != null ? dto.getReportDate() : labReport.getReportDate());
        labReport.setConductedBy(dto.getConductedBy() != null ? dto.getConductedBy() : labReport.getConductedBy());

        return labReportMapper.toDto(labReportRepository.save(labReport));
    }

    // Delete a lab report
    @Override
    public void deleteLabReport(String id) {
        LabReport labReport = labReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab Report not found"));

        labReportRepository.delete(labReport);
    }

    private void validateLabScientist(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab scientist not found"));

        if (user.getRole() != Role.LAB_SCIENTIST) {
            throw new UserNotAllowedException("Only a Lab Scientist is allowed to send a lab result");
        }
    }

    private void validateTestResults(List<LabTestResultDto> testResults, boolean enforceResults) {
        if (testResults == null || testResults.isEmpty()) {
            throw new UserNotAllowedException("At least one test with result is required");
        }

        for (LabTestResultDto item : testResults) {
            if (item == null || item.getTestName() == null || item.getTestName().isBlank()) {
                throw new UserNotAllowedException("Each test entry must include a test name");
            }
            if (enforceResults && (item.getResult() == null || item.getResult().isBlank())) {
                throw new UserNotAllowedException("Each selected test must include a result");
            }
            if (!enforceResults && item.getResult() != null && !item.getResult().isBlank()) {
                throw new UserNotAllowedException("Results must be submitted by a lab scientist");
            }
        }
    }

    private List<LabReportEntry> mapTestResults(List<LabTestResultDto> testResults, LabReport labReport) {
        return testResults.stream()
                .map(item -> LabReportEntry.builder()
                        .labReport(labReport)
                        .testName(item.getTestName().trim())
                        .result(item.getResult() == null ? "" : item.getResult().trim())
                        .build())
                .collect(Collectors.toList());
    }

    private User authenticatedUser(Authentication authentication, Role role) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails details)) {
            throw new UserNotAllowedException("Authentication is required");
        }
        User user = userRepository.findByEmailOrPhone(details.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != role) {
            throw new UserNotAllowedException("This operation requires role " + role);
        }
        return user;
    }
}
