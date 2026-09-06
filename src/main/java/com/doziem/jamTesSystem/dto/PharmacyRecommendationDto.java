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
public class PharmacyRecommendationDto {
    private String pharmacyId;
    private String pharmacyName;
    private Department department;
    private boolean mainPharmacy;
    private String mainPharmacyId;
    private String medicationId;
    private String medicationName;
    private int quantityInStock;
    private int reorderLevel;
    private double score;
    private String status;
    private String recommendationReason;
}
