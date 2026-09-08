package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Billing;
import com.doziem.jamTesSystem.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDto {

    private String id;
    private String patientId;
    private BigDecimal totalAmount;
    private boolean isPaid;
    private String paymentMethod;
    private LocalDate billingDate;

    // Convert DTO to Entity
}
