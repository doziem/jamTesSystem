package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatienceServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatienceService patienceService;

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
}
