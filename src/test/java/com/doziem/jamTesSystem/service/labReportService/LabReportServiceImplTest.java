package com.doziem.jamTesSystem.service.labReportService;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.dto.LabTestResultDto;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.mapper.LabReportMapper;
import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.LabReportEntry;
import com.doziem.jamTesSystem.model.LabRequest;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import com.doziem.jamTesSystem.repository.LabRequestRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabReportServiceImplTest {
    @Mock private LabReportRepository reports;
    @Mock private LabRequestRepository requests;
    @Mock private PatientRepository patients;
    @Mock private UserRepository users;
    @Mock private LabReportMapper mapper;
    @InjectMocks private LabReportServiceImpl service;

    private Authentication authentication(String email) {
        UserDetails principal = org.springframework.security.core.userdetails.User.withUsername(email)
                .password("unused").roles("DOCTOR").build();
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    void doctorCreatesPendingRequestLinkedToAuthenticatedUser() {
        User doctor = User.builder().id("user-1").role(Role.DOCTOR).build();
        Patient patient = Patient.builder().id("patient-1").build();
        LabReport report = LabReport.builder().build();
        LabReportDto dto = LabReportDto.builder().patientId("patient-1").requestedBy("forged")
                .testResults(List.of(LabTestResultDto.builder().testName("CBC").build())).build();
        when(users.findByEmailOrPhone("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(users.findById("user-1")).thenReturn(Optional.of(doctor));
        when(patients.findById("patient-1")).thenReturn(Optional.of(patient));
        when(requests.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toEntity(eq(dto), eq(patient), any(LabRequest.class))).thenAnswer(invocation -> {
            report.setLabRequest(invocation.getArgument(2));
            return report;
        });
        when(reports.save(report)).thenReturn(report);

        service.createLabReport(dto, authentication("doctor@example.com"));

        assertEquals(doctor, report.getLabRequest().getRequestedBy());
        assertEquals(patient, report.getLabRequest().getPatient());
        assertEquals("", report.getTestResults().get(0).getResult());
        assertNull(report.getReportDate());
    }

    @Test
    void scientistCompletesOnlyTheRequestedTests() {
        User scientist = User.builder().id("lab-1").role(Role.LAB_SCIENTIST).build();
        LabReport report = LabReport.builder().id("report-1")
                .testResults(new ArrayList<>(List.of(LabReportEntry.builder().testName("CBC").result("").build()))).build();
        when(users.findByEmailOrPhone("lab@example.com")).thenReturn(Optional.of(scientist));
        when(users.findById("lab-1")).thenReturn(Optional.of(scientist));
        when(reports.findById("report-1")).thenReturn(Optional.of(report));
        when(reports.save(report)).thenReturn(report);
        LabReportDto dto = LabReportDto.builder().conductedBy("forged")
                .testResults(List.of(LabTestResultDto.builder().testName("CBC").result("Normal").build())).build();

        service.updateLabReport("report-1", dto, authentication("lab@example.com"));

        assertEquals("lab-1", report.getConductedBy());
        assertEquals("Normal", report.getTestResults().get(0).getResult());
        assertNotNull(report.getReportDate());
        dto.setTestResults(List.of(LabTestResultDto.builder().testName("Other").result("Normal").build()));
        assertThrows(IllegalArgumentException.class, () -> service.updateLabReport("report-1", dto, authentication("lab@example.com")));
    }

    @Test
    void doctorCannotSubmitResults() {
        when(users.findByEmailOrPhone("doctor@example.com"))
                .thenReturn(Optional.of(User.builder().id("user-1").role(Role.DOCTOR).build()));
        assertThrows(UserNotAllowedException.class, () -> service.updateLabReport("report-1",
                LabReportDto.builder().build(), authentication("doctor@example.com")));
        verifyNoInteractions(reports);
    }
}
