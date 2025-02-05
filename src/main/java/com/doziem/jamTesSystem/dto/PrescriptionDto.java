package com.doziem.jamTesSystem.dto;


import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Prescription;

import java.util.UUID;

public class PrescriptionDto {
    private UUID id;
    private UUID patientId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String prescribedBy;
    private String prescriptionDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(String prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public String getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(String prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public PrescriptionDto(){}

    public PrescriptionDto(UUID id,UUID patientId, String dosage, String medicationName, String frequency,  String prescribedBy, String prescriptionDate) {
        this.id = id;
        this.patientId = patientId;
        this.dosage = dosage;
        this.medicationName = medicationName;
        this.frequency = frequency;

        this.prescribedBy = prescribedBy;
        this.prescriptionDate = prescriptionDate;
    }

    // Convert DTO to Entity
    public static Prescription mapToEntity(PrescriptionDto dto,Patient patient) {
        return new Prescription(
                dto.getId(),
                dto.getDosage(),
                dto.getFrequency(),
                dto.getMedicationName(),
                patient,
                dto.getPrescribedBy(),
                dto.getPrescriptionDate());
    }

    // Convert Entity to DTO
    public static PrescriptionDto mapToDTO(Prescription prescription) {
        return new PrescriptionDto(
                prescription.getId(),
                prescription.getPatient().getId(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getPrescribedBy(),
                prescription.getPrescriptionDate());
    }
}
