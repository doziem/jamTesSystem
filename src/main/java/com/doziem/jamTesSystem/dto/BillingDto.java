package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Billing;
import com.doziem.jamTesSystem.model.Patient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BillingDto {

    private UUID id;
    private UUID patientId;
    private BigDecimal totalAmount;
    private boolean isPaid;
    private String paymentMethod;
    private LocalDate billingDate;


    public BillingDto(){}

    public BillingDto(UUID id, UUID patientId, boolean isPaid, BigDecimal totalAmount, String paymentMethod, LocalDate billingDate) {
        this.id = id;
        this.patientId = patientId;
        this.isPaid = isPaid;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.billingDate = billingDate;
    }


    public UUID getId() { return id; }
    public UUID getPatientId() { return patientId; }
    public boolean isPaid() { return isPaid; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod;
}

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }

    public LocalDate getBillingDate() { return billingDate; }

    // Convert DTO to Entity
    public static Billing mapToEntity(BillingDto dto, Patient patient) {
        return new Billing(
                dto.getId(),
                patient,
                dto.isPaid(),
                dto.getTotalAmount(),
                dto.getPaymentMethod(),
                dto.getBillingDate());

    }

    // Convert Entity to DTO
    public static BillingDto mapToDTO(Billing billing) {
        return new BillingDto(
                billing.getId(),
                billing.getPatient().getId(),
                billing.isPaid(),
                billing.getTotalAmount(),
                billing.getPaymentMethod(),
                billing.getBillingDate());
    }
}
