package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.BillingDto;
import com.doziem.jamTesSystem.model.Billing;
import com.doziem.jamTesSystem.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class BillingMapper {
    public Billing toEntity(BillingDto dto, Patient patient) {
        return Billing.builder()
                .id(dto.getId())
                .patient(patient)
                .totalAmount(dto.getTotalAmount())
                .paid(dto.isPaid())
                .paymentMethod(dto.getPaymentMethod())
                .billingDate(dto.getBillingDate())
                .build();
    }

    public BillingDto toDto(Billing billing) {
        return BillingDto.builder()
                .id(billing.getId())
                .patientId(billing.getPatient() != null ? billing.getPatient().getId() : null)
                .totalAmount(billing.getTotalAmount())
                .isPaid(billing.isPaid())
                .paymentMethod(billing.getPaymentMethod())
                .billingDate(billing.getBillingDate())
                .build();
    }
}
