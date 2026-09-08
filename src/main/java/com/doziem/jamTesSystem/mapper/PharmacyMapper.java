package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.PharmacyDto;
import com.doziem.jamTesSystem.model.Pharmacy;
import org.springframework.stereotype.Component;

@Component
public class PharmacyMapper {
    public Pharmacy toEntity(PharmacyDto dto, Pharmacy mainPharmacyRef) {
        return Pharmacy.builder()
                .id(dto.getId())
                .name(dto.getName())
                .department(dto.getDepartment())
                .mainPharmacy(dto.isMainPharmacy())
                .mainPharmacyRef(mainPharmacyRef)
                .build();
    }

    public PharmacyDto toDto(Pharmacy pharmacy) {
        String parentId = pharmacy.getMainPharmacyRef() != null ? pharmacy.getMainPharmacyRef().getId() : null;
        return PharmacyDto.builder()
                .id(pharmacy.getId())
                .name(pharmacy.getName())
                .department(pharmacy.getDepartment())
                .mainPharmacy(pharmacy.isMainPharmacy())
                .mainPharmacyId(parentId)
                .build();
    }
}
