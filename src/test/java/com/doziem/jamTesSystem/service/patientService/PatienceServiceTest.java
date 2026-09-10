package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.EncounterDto;
import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.exceptions.InvalidResourceException;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.EncounterMapper;
import com.doziem.jamTesSystem.mapper.PatientMapper;
import com.doziem.jamTesSystem.mapper.DoctorMapper;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.Encounter;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.BillingRepository;
import com.doziem.jamTesSystem.repository.DoctorRepository;
import com.doziem.jamTesSystem.repository.EncounterRepository;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import com.doziem.jamTesSystem.request.EncounterStatusRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatienceServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private EncounterMapper encounterMapper;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private LabReportRepository labReportRepository;

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private PatienceService patienceService;

    @Test
    void createPatientSavesAndReturnsDto() {
        PatientDto request = new PatientDto();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane@example.com");
        request.setPhone("08012345678");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setGender("F");
        request.setActive(true);

        Patient patient = new Patient();
        patient.setId("p-1");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");
        patient.setEmail("jane@example.com");
        patient.setPhone("08012345678");
        patient.setDateOfBirth(LocalDate.of(2000, 1, 1));
        patient.setGender("F");
        patient.setActive(true);

        when(patientMapper.toEntity(any(PatientDto.class), any(Patient.class))).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toDto(patient)).thenReturn(request);

        PatientDto result = patienceService.createPatient(request);

        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void getPatientByIdReturnsDto() {
        Patient patient = new Patient();
        patient.setId("p-1");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");

        Encounter encounter = new Encounter();
        encounter.setId("e-1");
        encounter.setPatient(patient);
        encounter.setStatus("ARRIVED");

        EncounterDto encounterDto = EncounterDto.builder().id("e-1").patientId("p-1").status("ARRIVED").build();

        when(patientRepository.findById("p-1")).thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(buildPatientDto("p-1", "Jane", "Doe"));
        when(encounterRepository.findByPatientIdOrderByArrivalTimeDesc("p-1")).thenReturn(List.of(encounter));
        when(encounterMapper.toDto(encounter)).thenReturn(encounterDto);

        PatientDto result = patienceService.getPatientById("p-1");

        assertEquals("p-1", result.getId());
        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void getAllPatientsReturnsPagedList() {
        Patient patient = new Patient();
        patient.setId("p-1");
        patient.setFirstName("Jane");
        patient.setLastName("Doe");

        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(buildPatientDto("p-1", "Jane", "Doe"));

        List<PatientDto> result = patienceService.getAllPatients(0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void updatePatient_keepsExistingValuesAndSetsActiveState() {
        Patient existingPatient = new Patient();
        existingPatient.setId("p-1");
        existingPatient.setFirstName("Ada");
        existingPatient.setLastName("Lovelace");
        existingPatient.setEmail("ada@example.com");
        existingPatient.setPhone("1234567890");
        existingPatient.setDateOfBirth(LocalDate.of(1815, 12, 10));
        existingPatient.setGender("F");
        existingPatient.setActive(false);

        when(patientRepository.findById("p-1")).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(patientMapper.toDto(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            PatientDto dto = new PatientDto();
            dto.setId(patient.getId());
            dto.setFirstName(patient.getFirstName());
            dto.setLastName(patient.getLastName());
            dto.setEmail(patient.getEmail());
            dto.setPhone(patient.getPhone());
            dto.setDateOfBirth(patient.getDateOfBirth());
            dto.setGender(patient.getGender());
            dto.setActive(patient.isActive());
            return dto;
        });

        PatientDto updateRequest = new PatientDto();
        updateRequest.setFirstName("Grace");
        updateRequest.setLastName("Hopper");
        updateRequest.setEmail("grace@example.com");
        updateRequest.setPhone("0987654321");
        updateRequest.setDateOfBirth(LocalDate.of(1906, 12, 9));
        updateRequest.setGender("F");
        updateRequest.setActive(true);

        PatientDto updated = patienceService.updatePatient("p-1", updateRequest);

        assertEquals("Grace", updated.getFirstName());
        assertEquals("Hopper", updated.getLastName());
        assertEquals("grace@example.com", updated.getEmail());
        assertTrue(updated.isActive());
    }

    @Test
    void deletePatientDeletesWhenPresent() {
        Patient patient = new Patient();
        patient.setId("p-1");

        when(patientRepository.findById("p-1")).thenReturn(Optional.of(patient));

        patienceService.deletePatient("p-1");

        verify(patientRepository).delete(patient);
    }

    @Test
    void getPatientVisitHistoryReturnsEncounterDtos() {
        Patient patient = new Patient();
        patient.setId("p-1");

        Encounter encounter = new Encounter();
        encounter.setId("e-1");
        encounter.setPatient(patient);
        encounter.setStatus("ARRIVED");

        EncounterDto dto = EncounterDto.builder().id("e-1").patientId("p-1").status("ARRIVED").build();

        when(patientRepository.findById("p-1")).thenReturn(Optional.of(patient));
        when(encounterRepository.findByPatientIdOrderByArrivalTimeDesc("p-1")).thenReturn(List.of(encounter));
        when(encounterMapper.toDto(encounter)).thenReturn(dto);

        List<EncounterDto> result = patienceService.getPatientVisitHistory("p-1");

        assertEquals(1, result.size());
        assertEquals("e-1", result.get(0).getId());
    }

    @Test
    void updateEncounterStatusMovesToNextWorkflowState() {
        Patient patient = new Patient();
        patient.setId("p-1");

        Encounter encounter = new Encounter();
        encounter.setId("e-1");
        encounter.setPatient(patient);
        encounter.setStatus("ARRIVED");

        EncounterDto dto = EncounterDto.builder().id("e-1").patientId("p-1").status("TRIAGED").build();

        Doctor doctor = Doctor.builder().id("d-1").firstName("Jane").lastName("Doe").build();
        when(doctorRepository.findById("d-1")).thenReturn(Optional.of(doctor));
        when(encounterRepository.findById("e-1")).thenReturn(Optional.of(encounter));
        when(encounterRepository.save(any(Encounter.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(encounterMapper.toDto(any(Encounter.class))).thenReturn(dto);

        EncounterStatusRequest request = new EncounterStatusRequest();
        request.setStatus("TRIAGED");
        request.setAssignedDoctorId("d-1");
        request.setDepartmentName("Emergency");
        request.setTriageNotes("Vitals captured");

        EncounterDto result = patienceService.updateEncounterStatus("e-1", request);

        assertEquals("TRIAGED", result.getStatus());
    }

    @Test
    void updateEncounterStatusRejectsSkippedWorkflowState() {
        Patient patient = new Patient();
        patient.setId("p-1");

        Encounter encounter = new Encounter();
        encounter.setId("e-1");
        encounter.setPatient(patient);
        encounter.setStatus("ARRIVED");

        when(encounterRepository.findById("e-1")).thenReturn(Optional.of(encounter));

        EncounterStatusRequest request = new EncounterStatusRequest();
        request.setStatus("ADMITTED");

        assertThrows(InvalidResourceException.class,
                () -> patienceService.updateEncounterStatus("e-1", request));
    }

    @Test
    void getPatientByIdThrowsWhenMissing() {
        when(patientRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patienceService.getPatientById("missing"));
    }

    @Test
    void getAssignableDoctorsReturnsMappedDoctors() {
        Doctor doctor = Doctor.builder().id("d-1").firstName("Jane").lastName("Doe").build();
        DoctorDto doctorDto = DoctorDto.builder().id("d-1").fullName("Jane Doe").build();

        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);

        List<DoctorDto> result = patienceService.getAssignableDoctors();

        assertEquals(1, result.size());
        assertEquals("d-1", result.get(0).getId());
    }

    private PatientDto buildPatientDto(String id, String firstName, String lastName) {
        PatientDto dto = new PatientDto();
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        return dto;
    }
}
