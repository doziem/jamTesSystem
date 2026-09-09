package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.PharmacyInventoryDto;
import com.doziem.jamTesSystem.model.PharmacyInventory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PharmacyInventoryMapper {
    public PharmacyInventoryDto toDto(PharmacyInventory inventory) {
        return PharmacyInventoryDto.builder()
                .id(inventory.getId() != null ? inventory.getId().toString() : null)
                .pharmacyId(inventory.getPharmacy() != null && inventory.getPharmacy().getId() != null ? inventory.getPharmacy().getId().toString() : null)
                .medicationId(inventory.getMedication() != null && inventory.getMedication().getId() != null ? inventory.getMedication().getId().toString() : null)
                .medicationName(inventory.getMedication() != null ? inventory.getMedication().getName() : null)
                .quantityInStock(inventory.getQuantityInStock())
                .reorderLevel(inventory.getReorderLevel())
                .build();
    }
}
