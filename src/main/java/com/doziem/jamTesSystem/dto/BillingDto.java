package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Billing;
import com.doziem.jamTesSystem.model.Patient;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

public class BillingDto {
    private UUID id;
    private UUID patientId;
    private BigDecimal totalAmount;
    private boolean paid;
    private String paymentMethod;
    private String billingDate;

    public BillingDto(UUID id, UUID patientId, BigDecimal totalAmount, boolean paid, String paymentMethod, String billingDate) {
        this.id = id;
        this.patientId=patientId;
        this.totalAmount=totalAmount;
        this.paid = paid;
        this.paymentMethod = paymentMethod;
        this.billingDate=billingDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate;
    }
    public BillingDto(){}

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
                billing.getTotalAmount(),
                billing.isPaid(),
                billing.getPaymentMethod(),
                billing.getBillingDate());
    }
}
