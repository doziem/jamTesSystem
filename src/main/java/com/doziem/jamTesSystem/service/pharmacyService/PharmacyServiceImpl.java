package com.doziem.jamTesSystem.service.pharmacyService;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.dto.PharmacyDepartmentPerformanceDto;
import com.doziem.jamTesSystem.dto.PharmacyDto;
import com.doziem.jamTesSystem.dto.PharmacyInventoryDto;
import com.doziem.jamTesSystem.dto.PharmacyMedicationLevelDto;
import com.doziem.jamTesSystem.dto.PharmacyRecommendationDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.mapper.PharmacyInventoryMapper;
import com.doziem.jamTesSystem.mapper.PharmacyMapper;
import com.doziem.jamTesSystem.model.*;
import com.doziem.jamTesSystem.repository.*;
import com.doziem.jamTesSystem.service.emailService.EmailService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class PharmacyServiceImpl implements IPharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final PharmacyInventoryRepository pharmacyInventoryRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillingRepository billingRepository;
    private final EmailService emailService;
    private final PharmacyMapper pharmacyMapper;
    private final PharmacyInventoryMapper pharmacyInventoryMapper;

    public PharmacyServiceImpl(
            PharmacyRepository pharmacyRepository,
            PharmacyInventoryRepository pharmacyInventoryRepository,
            MedicationRepository medicationRepository,
            PrescriptionRepository prescriptionRepository,
            BillingRepository billingRepository,
            EmailService emailService,
            PharmacyMapper pharmacyMapper,
            PharmacyInventoryMapper pharmacyInventoryMapper) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyInventoryRepository = pharmacyInventoryRepository;
        this.medicationRepository = medicationRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.billingRepository = billingRepository;
        this.emailService = emailService;
        this.pharmacyMapper = pharmacyMapper;
        this.pharmacyInventoryMapper = pharmacyInventoryMapper;
    }

    @Override
    public PharmacyDto createMainPharmacy(PharmacyDto pharmacyDto) {
        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDto, null);
        pharmacy.setMainPharmacy(true);
        pharmacy.setDepartment(Department.MAIN_PHARMACY);
        return pharmacyMapper.toDto(pharmacyRepository.save(pharmacy));
    }

    @Override
    public PharmacyDto createDepartmentPharmacy(String mainPharmacyId, PharmacyDto pharmacyDto) {
        Pharmacy mainPharmacy = pharmacyRepository.findById(mainPharmacyId)
                .orElseThrow(() -> new ResourceNotFoundException("Main pharmacy not found"));
        if (!mainPharmacy.isMainPharmacy()) {
            throw new UserNotAllowedException("The selected pharmacy is not the main pharmacy");
        }

        Pharmacy pharmacy = pharmacyMapper.toEntity(pharmacyDto, mainPharmacy);
        pharmacy.setMainPharmacy(false);
        pharmacy.setMainPharmacyRef(mainPharmacy);
        return pharmacyMapper.toDto(pharmacyRepository.save(pharmacy));
    }

    @Override
    public List<PharmacyDto> getAllPharmacies() {
        return getAllPharmacies(0, 10, "name", "asc");
    }

    @Override
    public List<PharmacyDto> getAllPharmacies(int page, int size, String sortBy, String sortDirection) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        String property = (sortBy == null || sortBy.isBlank()) ? "name" : sortBy.trim();
        validatePharmacySortProperty(property);

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));

        return pharmacyRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(pharmacyMapper::toDto)
                .toList();
    }

    private void validatePharmacySortProperty(String sortBy) {
        try {
            Field field = Pharmacy.class.getDeclaredField(sortBy);
            if (field.isSynthetic()) {
                throw new IllegalArgumentException("Invalid sort field: " + sortBy);
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }
    }

    @Override
    public Optional<PharmacyDto> getPharmacyById(String id) {
        return pharmacyRepository.findById(id).map(pharmacyMapper::toDto);
    }
/**
 * Adds a specified quantity of medication to a pharmacy's inventory.
 * @param pharmacyId the ID of the pharmacy
 * @param medicationId the ID of the medication
 * @param quantity the quantity of medication to add
 * @return the updated PharmacyInventoryDto for the pharmacy
 * @throws ResourceNotFoundException if the pharmacy or medication is not found
 * @throws IllegalArgumentException if the quantity is less than or equal to zero
 */
    @Override
    public PharmacyInventoryDto addMedicineToPharmacy(String pharmacyId, String medicationId, int quantity) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found"));
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        PharmacyInventory inventory = pharmacyInventoryRepository.findByPharmacyAndMedication(pharmacy, medication)
                .orElseGet(() -> {
                    PharmacyInventory stock = new PharmacyInventory();
                    stock.setPharmacy(pharmacy);
                    stock.setMedication(medication);
                    stock.setQuantityInStock(0);
                    stock.setReorderLevel(10);
                    return stock;
                });

        inventory.setQuantityInStock(inventory.getQuantityInStock() + quantity);
        return pharmacyInventoryMapper.toDto(pharmacyInventoryRepository.save(inventory));
    }

/**
 * Transfers medication from the main pharmacy to a department pharmacy.
 * @param departmentPharmacyId the ID of the department pharmacy
 * @param medicationId the ID of the medication to transfer
 * @param quantity the quantity of medication to transfer
 * @return the updated PharmacyInventoryDto for the department pharmacy
 * @throws ResourceNotFoundException if the department pharmacy, main pharmacy, or medication is not found
 * @throws UserNotAllowedException if the main pharmacy does not have enough stock or if the department pharmacy is the main pharmacy
 */
    @Override
    public PharmacyInventoryDto transferMedicationFromMain(String departmentPharmacyId, String medicationId, int quantity) {
        Pharmacy departmentPharmacy = pharmacyRepository.findById(departmentPharmacyId)
                .orElseThrow(() -> new ResourceNotFoundException("Department pharmacy not found"));

        if (departmentPharmacy.isMainPharmacy()) {
            throw new UserNotAllowedException("Main pharmacy cannot pull stock from itself");
        }

        Pharmacy mainPharmacy = departmentPharmacy.getMainPharmacyRef();
        if (mainPharmacy == null) {
            throw new ResourceNotFoundException("This department has no main pharmacy source configured");
        }

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found"));

        PharmacyInventory mainStock = pharmacyInventoryRepository.findByPharmacyIdAndMedicationId(mainPharmacy.getId(), medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not available in main pharmacy"));

        if (mainStock.getQuantityInStock() < quantity) {
            throw new UserNotAllowedException("Main pharmacy does not have enough stock to transfer");
        }

        PharmacyInventory departmentStock = pharmacyInventoryRepository.findByPharmacyIdAndMedicationId(departmentPharmacyId, medicationId)
                .orElseGet(() -> {
                    PharmacyInventory stock = new PharmacyInventory();
                    stock.setPharmacy(departmentPharmacy);
                    stock.setMedication(medication);
                    stock.setQuantityInStock(0);
                    stock.setReorderLevel(10);
                    return stock;
                });

        mainStock.setQuantityInStock(mainStock.getQuantityInStock() - quantity);
        departmentStock.setQuantityInStock(departmentStock.getQuantityInStock() + quantity);
        pharmacyInventoryRepository.save(mainStock);
        return pharmacyInventoryMapper.toDto(pharmacyInventoryRepository.save(departmentStock));
    }
/**
 * Confirms payment and dispenses medication for a given prescription and pharmacy.
 *
 * @param prescriptionId the ID of the prescription
 * @param pharmacyId the ID of the pharmacy
 * @return a confirmation message
 */
    @Override
    public String confirmPaymentAndDispense(String prescriptionId, String pharmacyId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found"));

        if (prescription.getPharmacy() != null && !prescription.getPharmacy().getId().equals(pharmacyId)) {
            throw new UserNotAllowedException("This prescription belongs to another pharmacy");
        }

        Medication medication = medicationRepository.findByNameIgnoreCase(prescription.getMedicationName())
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found for prescription"));

        PharmacyInventory inventory = pharmacyInventoryRepository.findByPharmacyIdAndMedicationId(pharmacyId, medication.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medication unavailable in selected pharmacy"));

        if (inventory.getQuantityInStock() < prescription.getQuantity()) {
            throw new UserNotAllowedException("Selected pharmacy does not have enough stock for this prescription");
        }


        Patient patient = prescription.getPatient();
        if (patient == null) {
            throw new ResourceNotFoundException("Patient not found");
        }

        BigDecimal medicationCost = medication.getUnitPrice().multiply(BigDecimal.valueOf(prescription.getQuantity()));

        Billing billing = new Billing();
        billing.setPatient(patient);
        billing.setTotalAmount(medicationCost);
        billing.setPaid(true);
        billing.setPaymentMethod("Cash/Transfer");
        billing.setBillingDate(LocalDate.now());
        Billing savedBilling = billingRepository.save(billing);

        inventory.setQuantityInStock(inventory.getQuantityInStock() - prescription.getQuantity());
        pharmacyInventoryRepository.save(inventory);

        prescription.setPharmacy(pharmacy);
        prescription.setDepartment(pharmacy.getDepartment());
        prescription.setStatus("DISPENSED");
        prescription.setTotalCost(medicationCost);
        prescription.setPaymentConfirmed(true);
        prescriptionRepository.save(prescription);

        return "Medication dispensed successfully after patient payment confirmation. Cost: " + medicationCost + ". Billing ID: " + savedBilling.getId();
    }

/**
 * Recommends pharmacies for a given medication based on stock availability and other factors.
 * @param medicationId The ID of the medication to check.
 * @param minimumQuantity The minimum quantity required.
 * @return A list of recommended pharmacies with their stock status and recommendation reasons.
 */
    @Override
    public List<PharmacyRecommendationDto> recommendPharmaciesForMedication(String medicationId, int minimumQuantity) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found"));

        int requiredQuantity = Math.max(minimumQuantity, 0);

        return pharmacyRepository.findAll().stream()
                .map(pharmacy -> {
                    PharmacyInventory inventory = pharmacyInventoryRepository.findByPharmacyIdAndMedicationId(pharmacy.getId(), medicationId)
                            .orElse(null);

                    int quantityInStock = inventory != null ? inventory.getQuantityInStock() : 0;
                    int reorderLevel = inventory != null ? inventory.getReorderLevel() : 10;

                    String status;
                    String recommendationReason;
                    if (inventory == null || quantityInStock <= 0) {
                        status = "OUT_OF_STOCK";
                        recommendationReason = "No stock in this pharmacy for this medication.";
                    } else if (quantityInStock < reorderLevel) {
                        status = "LOW_STOCK";
                        recommendationReason = "Stock is below the reorder level; replenish or transfer supply.";
                    } else {
                        status = "AVAILABLE";
                        recommendationReason = pharmacy.isMainPharmacy()
                                ? "Main pharmacy has enough stock and is the preferred supply point."
                                : "Pharmacy has enough stock to fill the request.";
                    }

                    double score = 0;
                    if (quantityInStock > 0) {
                        score = quantityInStock * 10.0;
                    }
                    if (pharmacy.isMainPharmacy()) {
                        score += 25;
                    }
                    if (quantityInStock >= requiredQuantity) {
                        score += 30;
                    }
                    if (quantityInStock < reorderLevel && quantityInStock > 0) {
                        score -= 15;
                    }
                    if (quantityInStock <= 0) {
                        score = 0;
                    }

                    return PharmacyRecommendationDto.builder()
                            .pharmacyId(pharmacy.getId())
                            .pharmacyName(pharmacy.getName())
                            .department(pharmacy.getDepartment())
                            .mainPharmacy(pharmacy.isMainPharmacy())
                            .mainPharmacyId(pharmacy.getMainPharmacyRef() != null ? pharmacy.getMainPharmacyRef().getId() : null)
                            .medicationId(medication.getId())
                            .medicationName(medication.getName())
                            .quantityInStock(quantityInStock)
                            .reorderLevel(reorderLevel)
                            .score(Math.max(0, score))
                            .status(status)
                            .recommendationReason(recommendationReason)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(PharmacyRecommendationDto::getScore).reversed())
                .toList();
    }

/**
 * Retrieves the performance dashboard for all pharmacy departments.
 *
 * @return a list of PharmacyDepartmentPerformanceDto containing performance metrics for each department.
 */

    @Override
    public List<PharmacyDepartmentPerformanceDto> getDepartmentPerformanceDashboard() {
        return pharmacyRepository.findAll().stream()
                .map(pharmacy -> {
                    List<PharmacyInventory> inventoryList = pharmacyInventoryRepository.findByPharmacyId(pharmacy.getId());
                    int totalInventoryItems = inventoryList.size();
                    int lowStockItems = 0;
                    int outOfStockItems = 0;
                    int totalUnitsAvailable = 0;
                    int totalReorderAlerts = 0;

                    for (PharmacyInventory inventory : inventoryList) {
                        int stock = inventory.getQuantityInStock();
                        int reorderLevel = inventory.getReorderLevel();
                        totalUnitsAvailable += stock;

                        if (stock <= 0) {
                            outOfStockItems++;
                            totalReorderAlerts++;
                        } else if (stock < reorderLevel) {
                            lowStockItems++;
                            totalReorderAlerts++;
                        }
                    }

                    double stockHealthPercent = totalInventoryItems == 0
                            ? 100.0
                            : ((double) (totalInventoryItems - lowStockItems - outOfStockItems) / totalInventoryItems) * 100.0;

                    String status;
                    if (totalReorderAlerts == 0) {
                        status = "HEALTHY";
                    } else if (lowStockItems > 0 && outOfStockItems == 0) {
                        status = "WATCH_LIST";
                    } else if (outOfStockItems > 0) {
                        status = "CRITICAL";
                    } else {
                        status = "STABLE";
                    }

                    return PharmacyDepartmentPerformanceDto.builder()
                            .pharmacyId(pharmacy.getId())
                            .pharmacyName(pharmacy.getName())
                            .department(pharmacy.getDepartment())
                            .mainPharmacy(pharmacy.isMainPharmacy())
                            .totalInventoryItems(totalInventoryItems)
                            .lowStockItems(lowStockItems)
                            .outOfStockItems(outOfStockItems)
                            .totalUnitsAvailable(totalUnitsAvailable)
                            .totalReorderAlerts(totalReorderAlerts)
                            .stockHealthPercent(Math.max(0, Math.min(100, stockHealthPercent)))
                            .status(status)
                            .build();
                })
                .sorted(Comparator.comparing(PharmacyDepartmentPerformanceDto::getStatus, Comparator.comparingInt(status -> {
                    if ("CRITICAL".equals(status)) return 0;
                    if ("WATCH_LIST".equals(status)) return 1;
                    if ("STABLE".equals(status)) return 2;
                    return 3;
                })).thenComparing(PharmacyDepartmentPerformanceDto::getTotalReorderAlerts).reversed())
                .toList();
    }

    @Override
    public List<PharmacyMedicationLevelDto> getMedicationLevelByDepartment() {
        return pharmacyRepository.findAll().stream()
                .flatMap(pharmacy -> pharmacyInventoryRepository.findByPharmacyId(pharmacy.getId()).stream()
                        .map(inventory -> {
                            Medication medication = inventory.getMedication();
                            int quantity = inventory.getQuantityInStock();
                            String warningLevel = "NORMAL";
                            String warningMessage = "Sufficient stock available.";

                            if (quantity <= 1) {
                                warningLevel = "LEVEL_1";
                                warningMessage = "Critical: only 1 unit left. Immediate restock required.";
                                emailService.sendLowStockWarning(pharmacy.getName(), pharmacy.getDepartment().name(), medication.getName(), quantity);
                            } else if (quantity <= 5) {
                                warningLevel = "LEVEL_5";
                                warningMessage = "Urgent: stock is at 5 units or below. Reorder soon.";
                                emailService.sendLowStockWarning(pharmacy.getName(), pharmacy.getDepartment().name(), medication.getName(), quantity);
                            } else if (quantity <= 10) {
                                warningLevel = "LEVEL_10";
                                warningMessage = "Warning: stock is at 10 units or below. Prepare reorder.";
                                emailService.sendLowStockWarning(pharmacy.getName(), pharmacy.getDepartment().name(), medication.getName(), quantity);
                            }

                            return PharmacyMedicationLevelDto.builder()
                                    .pharmacyId(pharmacy.getId())
                                    .pharmacyName(pharmacy.getName())
                                    .department(pharmacy.getDepartment())
                                    .medicationId(medication.getId())
                                    .medicationName(medication.getName())
                                    .quantityInStock(quantity)
                                    .reorderLevel(inventory.getReorderLevel())
                                    .stockLevel(quantity <= 0 ? "OUT_OF_STOCK" : quantity <= 10 ? "LOW" : "AVAILABLE")
                                    .warningLevel(warningLevel)
                                    .warningMessage(warningMessage)
                                    .emailSent(!"NORMAL".equals(warningLevel))
                                    .build();
                        }))
                .toList();
    }
}
