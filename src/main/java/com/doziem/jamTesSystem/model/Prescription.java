package com.doziem.jamTesSystem.model;

import com.doziem.jamTesSystem.constant.Department;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name = "patient_id", columnDefinition = "uuid")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", columnDefinition = "uuid")
    private Pharmacy pharmacy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private int quantity = 1;

    @Column(nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private String frequency;

    private String prescribedBy;

    private String prescriptionDate;

    @Column(nullable = false)
    private String status = "PENDING_PHARMACY_REVIEW";

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean paymentConfirmed = false;

    @Column(nullable = false, columnDefinition = "numeric(19,2) default 0")
    private BigDecimal totalCost = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    public void ensureDefaults() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (status == null || status.isBlank()) {
            status = "PENDING_PHARMACY_REVIEW";
        }
        if (department == null) {
            department = Department.GENERAL;
        }
        if (quantity <= 0) {
            quantity = 1;
        }
        if (totalCost == null) {
            totalCost = BigDecimal.ZERO;
        }
    }

    public Prescription(String id, String dosage, String frequency, String medicationName,
                        Patient patient, String prescribedBy, String prescriptionDate) {
        this.id = id;
        this.dosage = dosage;
        this.frequency = frequency;
        this.medicationName = medicationName;
        this.patient = patient;
        this.prescribedBy = prescribedBy;
        this.prescriptionDate = prescriptionDate;
    }
}