package com.doziem.jamTesSystem.service.billingService;

import com.doziem.jamTesSystem.dto.BillingDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBillingService {
    BillingDto createBilling(BillingDto billingDto);
    Optional<BillingDto> getBillingById(UUID id);
    List<BillingDto> getAllBillings();
    List<BillingDto> getBillingsByPatientId(UUID patientId);
    BillingDto updateBilling(UUID id, BillingDto billingDto);
    void deleteBilling(UUID id);
}
