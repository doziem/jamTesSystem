package com.doziem.jamTesSystem.controller.pharmacyController;

import com.doziem.jamTesSystem.dto.PharmacyDepartmentPerformanceDto;
import com.doziem.jamTesSystem.dto.PharmacyDto;
import com.doziem.jamTesSystem.dto.PharmacyInventoryDto;
import com.doziem.jamTesSystem.dto.PharmacyMedicationLevelDto;
import com.doziem.jamTesSystem.dto.PharmacyReorderRequestDto;
import com.doziem.jamTesSystem.dto.PharmacyRecommendationDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.request.CreatePharmacyReorderRequest;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.pharmacyService.IPharmacyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyController {

    private final IPharmacyService pharmacyService;

    public PharmacyController(IPharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping("/main")
    public ResponseEntity<ApiResponse> createMainPharmacy(@RequestBody PharmacyDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Main pharmacy created", pharmacyService.createMainPharmacy(dto)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{mainPharmacyId}/department")
    public ResponseEntity<ApiResponse> createDepartmentPharmacy(@PathVariable String mainPharmacyId, @RequestBody PharmacyDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Department pharmacy created", pharmacyService.createDepartmentPharmacy(mainPharmacyId, dto)));
        } catch (ResourceNotFoundException | UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PharmacyDto>> getAllPharmacies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        return ResponseEntity.ok(pharmacyService.getAllPharmacies(page, size, sortBy, sortDirection));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPharmacyById(@PathVariable String id) {
        Optional<PharmacyDto> pharmacy = pharmacyService.getPharmacyById(id);
        return pharmacy.map(pharmacyDto -> ResponseEntity.ok(new ApiResponse(true, "Pharmacy fetched", pharmacyDto))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, "Pharmacy not found")));
    }

    @PostMapping("/{pharmacyId}/medications/{medicationId}/stock")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse> addStock(@PathVariable String pharmacyId, @PathVariable String medicationId, @RequestParam int quantity) {
        try {
            PharmacyInventoryDto inventory = pharmacyService.addMedicineToPharmacy(pharmacyId, medicationId, quantity);
            return ResponseEntity.ok(new ApiResponse(true, "Medicine stock added", inventory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{pharmacyId}/medications/{medicationId}/reorder-requests")
    public ResponseEntity<ApiResponse> createReorderRequest(
            @PathVariable String pharmacyId,
            @PathVariable String medicationId,
            @Valid @RequestBody CreatePharmacyReorderRequest request,
            Authentication authentication) {
        PharmacyReorderRequestDto created = pharmacyService.createReorderRequest(
                pharmacyId, medicationId, request.getQuantity(), authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Reorder request submitted", created));
    }

    @GetMapping("/reorder-requests")
    public ResponseEntity<ApiResponse> getReorderRequests(Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse(
                true, "Reorder requests fetched", pharmacyService.getReorderRequests(authentication)));
    }

    @PostMapping("/reorder-requests/{requestId}/process")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse> processReorderRequest(
            @PathVariable String requestId,
            Authentication authentication) {
        PharmacyReorderRequestDto processed = pharmacyService.processReorderRequest(requestId, authentication);
        return ResponseEntity.ok(new ApiResponse(true, "Reorder request fulfilled", processed));
    }

    @PostMapping("/{mainPharmacyId}/restock-from-inventory/{medicationId}")
    public ResponseEntity<ApiResponse> restockFromInventory(@PathVariable String mainPharmacyId, @PathVariable String medicationId, @RequestParam int quantity) {
        try {
            PharmacyInventoryDto inventory = pharmacyService.restockMainPharmacyFromInventory(mainPharmacyId, medicationId, quantity);
            return ResponseEntity.ok(new ApiResponse(true, "Main pharmacy restocked from inventory", inventory));
        } catch (ResourceNotFoundException | UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{departmentPharmacyId}/transfer/{medicationId}")
    public ResponseEntity<ApiResponse> transferStock(@PathVariable String departmentPharmacyId, @PathVariable String medicationId, @RequestParam int quantity) {
        try {
            PharmacyInventoryDto inventory = pharmacyService.transferMedicationFromMain(departmentPharmacyId, medicationId, quantity);
            return ResponseEntity.ok(new ApiResponse(true, "Stock transferred from main pharmacy", inventory));
        } catch (ResourceNotFoundException | UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{pharmacyId}/prescriptions/{prescriptionId}/payment-confirmation")
    public ResponseEntity<ApiResponse> confirmPaymentAndDispense(@PathVariable String pharmacyId, @PathVariable String prescriptionId) {
        try {
            String result = pharmacyService.confirmPaymentAndDispense(prescriptionId, pharmacyId);
            return ResponseEntity.ok(new ApiResponse(true, result));
        } catch (ResourceNotFoundException | UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/recommendations/{medicationId}")
    public ResponseEntity<ApiResponse> recommendPharmacies(
            @PathVariable String medicationId,
            @RequestParam(defaultValue = "1") int minimumQuantity) {
        try {
            List<PharmacyRecommendationDto> recommendations = pharmacyService.recommendPharmaciesForMedication(medicationId, minimumQuantity);
            return ResponseEntity.ok(new ApiResponse(true, "Pharmacy recommendations fetched", recommendations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/dashboard/department-performance")
    public ResponseEntity<ApiResponse> getDepartmentPerformanceDashboard() {
        List<PharmacyDepartmentPerformanceDto> dashboard = pharmacyService.getDepartmentPerformanceDashboard();
        return ResponseEntity.ok(new ApiResponse(true, "Department performance dashboard fetched", dashboard));
    }

    @GetMapping("/dashboard/medication-levels")
    public ResponseEntity<ApiResponse> getMedicationLevelsByDepartment() {
        List<PharmacyMedicationLevelDto> medicationLevels = pharmacyService.getMedicationLevelByDepartment();
        return ResponseEntity.ok(new ApiResponse(true, "Medication availability by pharmacy department fetched", medicationLevels));
    }

    @GetMapping("/{pharmacyId}/medications")
    public ResponseEntity<ApiResponse> getMedicationLevelsByPharmacy(@PathVariable String pharmacyId) {
        try {
            List<PharmacyMedicationLevelDto> medicationLevels = pharmacyService.getMedicationLevelsByPharmacy(pharmacyId);
            return ResponseEntity.ok(new ApiResponse(true, "Medication inventory for pharmacy fetched", medicationLevels));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
