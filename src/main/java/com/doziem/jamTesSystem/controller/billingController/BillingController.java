package com.doziem.jamTesSystem.controller.billingController;

import com.doziem.jamTesSystem.dto.BillingDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.billingService.IBillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final IBillingService billingService;

    public BillingController(IBillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createBilling(@RequestBody BillingDto billingDto) {
        try {
            BillingDto savedBilling = billingService.createBilling(billingDto);
            return ResponseEntity.ok().body(new ApiResponse(true, "Billing Created", savedBilling));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage()));
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingDto> getBillingById(@PathVariable UUID id) {
        Optional<BillingDto> billing = billingService.getBillingById(id);
        return billing.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/all")
    public ResponseEntity<List<BillingDto>> getAllBillings() {
        List<BillingDto> billings = billingService.getAllBillings();
        return ResponseEntity.ok(billings);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ApiResponse>> getBillingsByPatientId(@PathVariable UUID patientId) {
        try {
            List<BillingDto> billings = billingService.getBillingsByPatientId(patientId);
            return ResponseEntity.ok().body(Collections.singletonList(new ApiResponse(true, "All Patient Bills Fetched", billings)));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(new ApiResponse(false, e.getMessage())));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(new ApiResponse(false, e.getMessage())));
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBilling(@PathVariable UUID id, @RequestBody BillingDto billingDto) {
       try {
           BillingDto updatedBilling = billingService.updateBilling(id, billingDto);
           return ResponseEntity.ok().body(new ApiResponse(true,"Billing successfully updated",updatedBilling));
       }catch (ResourceNotFoundException e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false,e.getMessage()));
    }catch(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage()));
    }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBilling(@PathVariable UUID id) {
        billingService.deleteBilling(id);
        return ResponseEntity.noContent().build();
    }
}
