package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.constant.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyMedicationLevelDto {
    private String pharmacyId;
    private String pharmacyName;
    private Department department;
    private String medicationId;
    private String medicationName;
    private int quantityInStock;
    private int reorderLevel;
    private String stockLevel;
    private String warningLevel;
    private String warningMessage;
    private boolean emailSent;
}
