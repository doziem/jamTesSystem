package com.doziem.jamTesSystem.service.billingService;

import com.doziem.jamTesSystem.dto.BillingDto;
import com.doziem.jamTesSystem.response.ApiResponse;

import java.util.List;
import java.util.Optional;

public interface IBillingService {
    BillingDto createBilling(BillingDto billingDto);
    Optional<BillingDto> getBillingById(String id);
    List<BillingDto> getAllBillings();
    List<BillingDto> getBillingsByPatientId(String patientId);
    BillingDto updateBilling(String id, BillingDto billingDto);
    ApiResponse deleteBilling(String id);
}
