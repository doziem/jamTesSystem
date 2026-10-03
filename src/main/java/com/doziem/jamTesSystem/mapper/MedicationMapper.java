package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.MedicationDto;
import com.doziem.jamTesSystem.model.Medication;
import org.springframework.stereotype.Component;

@Component
public class MedicationMapper {

    public static MedicationDto toDto(Medication medication) {
        return MedicationDto.builder()
                .id(medication.getId() != null ? medication.getId().toString() : null)
                .name(medication.getName())
                .category(medication.getCategory())
                .unitPrice(medication.getUnitPrice())
                .active(medication.isActive())
                .quantity(medication.getQuantity())
                .build();
    }

    public static Medication toEntity(MedicationDto dto) {
        return Medication.builder()
                .id(dto.getId())
                .name(dto.getName())
                .category(dto.getCategory())
                .unitPrice(dto.getUnitPrice())
                .active(dto.isActive())
                .quantity(dto.getQuantity())

                .build();
    }
}
