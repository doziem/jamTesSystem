package com.doziem.jamTesSystem.service.medication;

import com.doziem.jamTesSystem.dto.MedicationDto;
import com.doziem.jamTesSystem.mapper.MedicationMapper;
import com.doziem.jamTesSystem.model.Medication;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.repository.MedicationRepository;
import com.doziem.jamTesSystem.repository.PharmacyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceTest {
//    create a test for the MedicationService class, mock the dependencies and test the getAllMedications method

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private MedicationMapper medicationMapper;

    @InjectMocks
    private MedicationService medicationService;

    @Test
    void createMedicationSavesMappedEntity() {
        // Implement the test logic here
        MedicationDto dto = MedicationDto.builder()
                .id("m-1")
                .name("Aspirin")
                .category("Painkiller")
                .unitPrice(BigDecimal.valueOf(10.0))
                .active(true)
                .quantity(50)
                .build();

        Pharmacy pharmacy = Pharmacy.builder()
                .id("p-1")
                .name("Pharmacy A")
                .build();

        Medication medication = Medication.builder()
                .id("med-001")
                .name("Paracetamol")
                .unitPrice(BigDecimal.valueOf(500))
                .category("Painkiller")
                .active(true)
                .quantity(20)
                .build();

       when(medicationRepository.save(medication)).thenReturn(medication);
        when(MedicationMapper.toEntity(dto)).thenReturn(medication);
        when(pharmacyRepository.findById("p-1")).thenReturn(Optional.of(pharmacy));


    }
}