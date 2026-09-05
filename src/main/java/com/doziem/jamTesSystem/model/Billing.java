package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name = "patient_id", columnDefinition = "uuid")
    private Patient patient;

    @Column(nullable = false, columnDefinition = "numeric(19,2) default 0")
    private BigDecimal totalAmount;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean paid;

    private String paymentMethod;

    private LocalDate billingDate;

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}