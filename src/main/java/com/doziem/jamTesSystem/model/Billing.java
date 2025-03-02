package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private boolean paid;

    private String paymentMethod;

    private LocalDate billingDate;
    public Billing(){}

    public Billing(UUID id,  Patient patient,boolean paid, BigDecimal totalAmount, String paymentMethod, LocalDate billingDate) {
        this.id = id;
        this.patient = patient;
        this.paid = paid;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.billingDate = billingDate;
    }

    public UUID getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public boolean isPaid() {
        return paid;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getBillingDate() {
        return billingDate;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }
}
