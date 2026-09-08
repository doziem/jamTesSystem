package com.doziem.jamTesSystem.service.prescriptionService;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.PrescriptionMapper;
import com.doziem.jamTesSystem.model.Medication;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.model.Prescription;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.MedicationRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.PharmacyRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    @Test
    void getAllPrescriptionsReturnsMappedList() {
        Prescription prescription = Prescription.builder().id("rx-1").build();
        when(prescriptionRepository.findAll()).thenReturn(List.of(prescription));
        when(prescriptionMapper.toDto(prescription)).thenReturn(PrescriptionDto.builder().id("rx-1").build());

        List<PrescriptionDto> result = prescriptionService.getAllPrescriptions();

        assertEquals(1, result.size());
    }

    @Test
    void getPrescriptionByIdReturnsPrescription() {
        Prescription prescription = Prescription.builder().id("rx-1").build();
        when(prescriptionRepository.findById("rx-1")).thenReturn(Optional.of(prescription));
        when(prescriptionMapper.toDto(prescription)).thenReturn(PrescriptionDto.builder().id("rx-1").build());

        PrescriptionDto result = prescriptionService.getPrescriptionById("rx-1").orElseThrow();

        assertEquals("rx-1", result.getId());
    }

    @Test
    void prescribeMedicationSavesWithCanonicalMedicationName() {
        Medication medication = Medication.builder().id("m-1").name("Paracetamol").unitPrice(BigDecimal.TEN).build();
        Patient patient = Patient.builder().id("p-1").build();
        User doctor = User.builder().id("d-1").role(Role.DOCTOR).build();
        Prescription prescription = Prescription.builder().id("rx-1").patient(patient).medicationName("Paracetamol").build();

        when(userRepository.findById("d-1")).thenReturn(Optional.of(doctor));
        when(medicationRepository.findByNameIgnoreCase("paracetamol")).thenReturn(Optional.of(medication));
        when(patientRepository.findById("p-1")).thenReturn(Optional.of(patient));
        when(prescriptionMapper.toEntity(any(PrescriptionDto.class), eq(patient), nullable(Pharmacy.class))).thenReturn(prescription);
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);
        when(prescriptionMapper.toDto(prescription)).thenReturn(PrescriptionDto.builder().id("rx-1").medicationName("Paracetamol").build());

        PrescriptionDto result = prescriptionService.prescribeMedication(PrescriptionDto.builder()
                .patientId("p-1")
                .prescribedBy("d-1")
                .medicationName("paracetamol")
                .build(), null);

        assertEquals("Paracetamol", result.getMedicationName());
    }

    @Test
    void updatePrescriptionUpdatesMutableFields() {
        Patient patient = Patient.builder().id("p-1").build();
        Prescription existing = Prescription.builder()
                .id("rx-1")
                .patient(patient)
                .medicationName("Paracetamol")
                .dosage("5mg")
                .frequency("Daily")
                .status("PENDING_PHARMACY_REVIEW")
                .prescribedBy("d-1")
                .build();
        when(prescriptionRepository.findById("rx-1")).thenReturn(Optional.of(existing));
        when(patientRepository.findById("p-1")).thenReturn(Optional.of(patient));
        when(medicationRepository.findByNameIgnoreCase("Ibuprofen")).thenReturn(Optional.of(Medication.builder().name("Ibuprofen").build()));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(prescriptionMapper.toDto(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription p = invocation.getArgument(0);
            return PrescriptionDto.builder().id(p.getId()).medicationName(p.getMedicationName()).dosage(p.getDosage()).build();
        });

        PrescriptionDto result = prescriptionService.updatePrescription("rx-1", PrescriptionDto.builder()
                .patientId("p-1")
                .medicationName("Ibuprofen")
                .dosage("10mg")
                .build(), null);

        assertEquals("Ibuprofen", result.getMedicationName());
        assertEquals("10mg", result.getDosage());
    }

    @Test
    void deletePrescriptionDeletesById() {
        prescriptionService.deletePrescription("rx-1");
        verify(prescriptionRepository).deleteById("rx-1");
    }
}
