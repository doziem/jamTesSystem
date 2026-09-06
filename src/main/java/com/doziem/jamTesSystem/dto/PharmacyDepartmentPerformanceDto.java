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
public class PharmacyDepartmentPerformanceDto {
    private String pharmacyId;
    private String pharmacyName;
    private Department department;
    private boolean mainPharmacy;
    private int totalInventoryItems;
    private int lowStockItems;
    private int outOfStockItems;
    private int totalUnitsAvailable;
    private int totalReorderAlerts;
    private double stockHealthPercent;
    private String status;
}
