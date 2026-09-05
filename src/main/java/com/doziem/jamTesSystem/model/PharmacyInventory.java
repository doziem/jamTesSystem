package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "pharmacy_inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pharmacy_id", "medication_id"})
)
public class PharmacyInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false, columnDefinition = "uuid")
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false, columnDefinition = "uuid")
    private Medication medication;

    @Column(nullable = false)
    private int quantityInStock;

    @Column(nullable = false)
    private int reorderLevel;

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

}