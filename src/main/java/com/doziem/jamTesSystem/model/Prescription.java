package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private String frequency;

    private String prescribedBy; // Doctor's name

    private String prescriptionDate;

    public Prescription() {
    }

    public Prescription(UUID id, String dosage, String frequency, String medicationName, Patient patient, String prescribedBy, String prescriptionDate) {
        this.id = id;
        this.dosage = dosage;
        this.frequency = frequency;
        this.medicationName = medicationName;
        this.patient = patient;
        this.prescribedBy = prescribedBy;
        this.prescriptionDate = prescriptionDate;
    }

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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(String prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(String prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
}
