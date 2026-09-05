package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.model.Prescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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


    public static Prescription mapToEntity(PrescriptionDto dto, Patient patient) {
        return mapToEntity(dto, patient, null);
    }

    public static Prescription mapToEntity(PrescriptionDto dto, Patient patient, Pharmacy pharmacy) {
        Prescription prescription = new Prescription(
                dto.getId(),
                dto.getDosage(),
                dto.getFrequency(),
                dto.getMedicationName(),
                patient,
                dto.getPrescribedBy(),
                dto.getPrescriptionDate()
        );
        prescription.setPharmacy(pharmacy);
        prescription.setDepartment(dto.getDepartment() != null ? dto.getDepartment() : Department.GENERAL);
        prescription.setQuantity(dto.getQuantity() > 0 ? dto.getQuantity() : 1);
        prescription.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING_PHARMACY_REVIEW");
        prescription.setPaymentConfirmed(dto.isPaymentConfirmed());
        prescription.setTotalCost(dto.getTotalCost() != null ? dto.getTotalCost() : BigDecimal.ZERO);
        prescription.setTotalCost(prescription.getTotalCost() == null ? BigDecimal.ZERO : prescription.getTotalCost());
        return prescription;
    }

    public static PrescriptionDto mapToDTO(Prescription prescription) {
        Pharmacy pharmacy = prescription.getPharmacy();
        return new PrescriptionDto(
                prescription.getId(),
                prescription.getPatient() != null ? prescription.getPatient().getId() : null,
                pharmacy != null ? pharmacy.getId() : null,
                prescription.getDepartment(),
                prescription.getQuantity(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getPrescribedBy(),
                prescription.getPrescriptionDate(),
                prescription.getStatus(),
                prescription.isPaymentConfirmed(),
                prescription.getTotalCost()
        );
    }
}
