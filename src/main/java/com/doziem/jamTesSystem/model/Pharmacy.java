package com.doziem.jamTesSystem.model;

import com.doziem.jamTesSystem.constant.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "pharmacies")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private boolean mainPharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_pharmacy_id")
    private Pharmacy mainPharmacyRef;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PharmacyInventory> inventory = new ArrayList<>();

    public Pharmacy() {
    }

    public Pharmacy(String id, String name, Department department, boolean mainPharmacy, Pharmacy mainPharmacyRef) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.mainPharmacy = mainPharmacy;
        this.mainPharmacyRef = mainPharmacyRef;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}