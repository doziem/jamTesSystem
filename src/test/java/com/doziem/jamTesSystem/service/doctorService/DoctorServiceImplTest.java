package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDashboardDto;
import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.mapper.DoctorMapper;
import com.doziem.jamTesSystem.mapper.LabReportMapper;
import com.doziem.jamTesSystem.mapper.PrescriptionMapper;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.LabRequest;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Prescription;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.DoctorRepository;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private LabReportRepository labReportRepository;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @Mock
    private LabReportMapper labReportMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void createDoctorSavesMappedEntity() {
        DoctorDto dto = DoctorDto.builder().userId("u-1").firstName("John").lastName("Doe").build();
        User user = User.builder().id("u-1").build();
        Doctor doctor = Doctor.builder().id("d-1").firstName("John").lastName("Doe").build();

        when(userRepository.findById("u-1")).thenReturn(Optional.of(user));
        when(doctorMapper.toEntity(dto, user)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.toDto(doctor)).thenReturn(DoctorDto.builder().id("d-1").firstName("John").lastName("Doe").build());

        DoctorDto result = doctorService.createDoctor(dto);

        assertEquals("d-1", result.getId());
    }

    @Test
    void getAllDoctorsReturnsPagedList() {
        Doctor doctor = Doctor.builder().id("d-1").firstName("John").build();
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(doctorMapper.toDto(doctor)).thenReturn(DoctorDto.builder().id("d-1").firstName("John").build());

        List<DoctorDto> result = doctorService.getAllDoctors();

        assertEquals(1, result.size());
    }

    @Test
    void getDoctorByIdReturnsDoctor() {
        Doctor doctor = Doctor.builder().id("d-1").firstName("John").build();
        when(doctorRepository.findById("d-1")).thenReturn(Optional.of(doctor));
        when(doctorMapper.toDto(doctor)).thenReturn(DoctorDto.builder().id("d-1").firstName("John").build());

        DoctorDto result = doctorService.getDoctorById("d-1");

        assertEquals("d-1", result.getId());
    }

    @Test
    void updateDoctorUpdatesStoredFields() {
        Doctor existing = Doctor.builder().id("d-1").firstName("John").lastName("Doe").build();
        when(doctorRepository.findById("d-1")).thenReturn(Optional.of(existing));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(doctorMapper.toDto(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor doctor = invocation.getArgument(0);
            return DoctorDto.builder().id(doctor.getId()).firstName(doctor.getFirstName()).lastName(doctor.getLastName()).build();
        });

        DoctorDto result = doctorService.updateDoctor("d-1", DoctorDto.builder().firstName("Jane").lastName("Smith").build());

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void deleteDoctorDeletesWhenPresent() {
        when(doctorRepository.existsById("d-1")).thenReturn(true);

        doctorService.deleteDoctor("d-1");

        org.mockito.Mockito.verify(doctorRepository).deleteById("d-1");
    }

    @Test
    void getDashboardBuildsSummary() {
        Doctor doctor = Doctor.builder().id("d-1").firstName("John").lastName("Doe").specialization("GP").build();
        Patient patient = Patient.builder().id("p-1").build();
        Prescription prescription = Prescription.builder()
                .id("rx-1")
                .patient(patient)
                .prescribedBy("d-1")
                .status("PENDING_PHARMACY_REVIEW")
                .prescriptionDate("2026-09-08")
                .build();
        LabRequest labRequest = LabRequest.builder().id("lrq-1").requestedBy(User.builder().id("d-1").build()).build();
        LabReport labReport = LabReport.builder().id("lr-1").patient(patient).labRequest(labRequest).testName("CBC").build();

        when(doctorRepository.findById("d-1")).thenReturn(Optional.of(doctor));
        when(prescriptionRepository.findAll()).thenReturn(List.of(prescription));
        when(labReportRepository.findByLabRequestRequestedById("d-1")).thenReturn(List.of(labReport));
        when(prescriptionMapper.toDto(prescription)).thenReturn(null);
        when(labReportMapper.toDto(labReport)).thenReturn(null);

        DoctorDashboardDto result = doctorService.getDashboard("d-1");

        assertEquals("d-1", result.getDoctorId());
        assertEquals(1, result.getTotalPrescriptions());
        assertEquals(1, result.getPendingPrescriptions());
    }
}
