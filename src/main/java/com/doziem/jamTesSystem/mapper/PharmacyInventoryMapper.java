package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.PharmacyInventoryDto;
import com.doziem.jamTesSystem.model.PharmacyInventory;
import org.springframework.stereotype.Component;

@Component
public class PharmacyInventoryMapper {
    public PharmacyInventoryDto toDto(PharmacyInventory inventory) {
        return PharmacyInventoryDto.builder()
                .id(inventory.getId())
                .pharmacyId(inventory.getPharmacy() != null ? inventory.getPharmacy().getId() : null)
                .medicationId(inventory.getMedication() != null ? inventory.getMedication().getId() : null)
                .medicationName(inventory.getMedication() != null ? inventory.getMedication().getName() : null)
                .quantityInStock(inventory.getQuantityInStock())
                .reorderLevel(inventory.getReorderLevel())
                .build();
    }
}
