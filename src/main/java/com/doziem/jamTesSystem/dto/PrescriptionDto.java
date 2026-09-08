package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionDto {
    private String id;
    private String patientId;
    private String pharmacyId;
    private Department department;
    private int quantity;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String prescribedBy;
    private String prescriptionDate;
    private String status;
    private boolean paymentConfirmed;
    private BigDecimal totalCost;


}
