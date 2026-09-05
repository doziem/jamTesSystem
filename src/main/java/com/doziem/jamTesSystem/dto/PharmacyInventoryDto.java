package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Medication;
import com.doziem.jamTesSystem.model.PharmacyInventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyInventoryDto {

    private String id;
    private String pharmacyId;
    private String medicationId;
    private String medicationName;
    private int quantityInStock;
    private int reorderLevel;

    public static PharmacyInventoryDto mapToDTO(PharmacyInventory inventory) {
        Medication medication = inventory.getMedication();
        return new PharmacyInventoryDto(
                inventory.getId(),
                inventory.getPharmacy().getId(),
                medication.getId(),
                medication.getName(),
                inventory.getQuantityInStock(),
                inventory.getReorderLevel()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}
