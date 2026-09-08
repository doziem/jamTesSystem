package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.model.Prescription;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PrescriptionMapper {
    public Prescription toEntity(PrescriptionDto dto, Patient patient, Pharmacy pharmacy) {
        BigDecimal totalCost = dto.getTotalCost() != null ? dto.getTotalCost() : BigDecimal.ZERO;
        return Prescription.builder()
                .id(dto.getId())
                .patient(patient)
                .pharmacy(pharmacy)
                .department(dto.getDepartment() != null ? dto.getDepartment() : Department.GENERAL)
                .quantity(dto.getQuantity() > 0 ? dto.getQuantity() : 1)
                .medicationName(dto.getMedicationName())
                .dosage(dto.getDosage())
                .frequency(dto.getFrequency())
                .prescribedBy(dto.getPrescribedBy())
                .prescriptionDate(dto.getPrescriptionDate())
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING_PHARMACY_REVIEW")
                .paymentConfirmed(dto.isPaymentConfirmed())
                .totalCost(totalCost)
                .build();
    }

    public PrescriptionDto toDto(Prescription prescription) {
        Pharmacy pharmacy = prescription.getPharmacy();
        return PrescriptionDto.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatient() != null ? prescription.getPatient().getId() : null)
                .pharmacyId(pharmacy != null ? pharmacy.getId() : null)
                .department(prescription.getDepartment())
                .quantity(prescription.getQuantity())
                .medicationName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .prescribedBy(prescription.getPrescribedBy())
                .prescriptionDate(prescription.getPrescriptionDate())
                .status(prescription.getStatus())
                .paymentConfirmed(prescription.isPaymentConfirmed())
                .totalCost(prescription.getTotalCost())
                .build();
    }
}
