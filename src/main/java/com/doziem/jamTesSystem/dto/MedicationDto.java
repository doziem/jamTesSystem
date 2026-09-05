package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Medication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationDto {

    private String id;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private boolean active;

    public static MedicationDto mapToDTO(Medication medication) {
        return new MedicationDto(
                medication.getId(),
                medication.getName(),
                medication.getCategory(),
                medication.getUnitPrice(),
                medication.isActive()
        );
    }

    public static Medication mapToEntity(MedicationDto dto) {
        Medication medication = new Medication();
        medication.setName(dto.getName());
        medication.setCategory(dto.getCategory());
        medication.setUnitPrice(dto.getUnitPrice());
        medication.setActive(dto.isActive());
        return medication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
