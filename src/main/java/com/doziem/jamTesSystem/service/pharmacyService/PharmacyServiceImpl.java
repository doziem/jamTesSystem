package com.doziem.jamTesSystem.service.pharmacyService;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.dto.PharmacyDto;
import com.doziem.jamTesSystem.dto.PharmacyInventoryDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.model.*;
import com.doziem.jamTesSystem.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PharmacyServiceImpl implements IPharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final PharmacyInventoryRepository pharmacyInventoryRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillingRepository billingRepository;

    public PharmacyServiceImpl(
            PharmacyRepository pharmacyRepository,
            PharmacyInventoryRepository pharmacyInventoryRepository,
            MedicationRepository medicationRepository,
            PrescriptionRepository prescriptionRepository,
            BillingRepository billingRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyInventoryRepository = pharmacyInventoryRepository;
        this.medicationRepository = medicationRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.billingRepository = billingRepository;
    }

    @Override
    public PharmacyDto createMainPharmacy(PharmacyDto pharmacyDto) {
        Pharmacy pharmacy = PharmacyDto.mapToEntity(pharmacyDto, null);
        pharmacy.setMainPharmacy(true);
        pharmacy.setDepartment(Department.MAIN_PHARMACY);
        return PharmacyDto.mapToDTO(pharmacyRepository.save(pharmacy));
    }

    @Override
    public PharmacyDto createDepartmentPharmacy(String mainPharmacyId, PharmacyDto pharmacyDto) {
        Pharmacy mainPharmacy = pharmacyRepository.findById(mainPharmacyId)
                .orElseThrow(() -> new ResourceNotFoundException("Main pharmacy not found"));
        if (!mainPharmacy.isMainPharmacy()) {
            throw new UserNotAllowedException("The selected pharmacy is not the main pharmacy");
        }

        Pharmacy pharmacy = PharmacyDto.mapToEntity(pharmacyDto, mainPharmacy);
        pharmacy.setMainPharmacy(false);
        pharmacy.setMainPharmacyRef(mainPharmacy);
        return PharmacyDto.mapToDTO(pharmacyRepository.save(pharmacy));
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
                .map(PharmacyDto::mapToDTO)
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
        return pharmacyRepository.findById(id).map(PharmacyDto::mapToDTO);
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
        return PharmacyInventoryDto.mapToDTO(pharmacyInventoryRepository.save(inventory));
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
        return PharmacyInventoryDto.mapToDTO(pharmacyInventoryRepository.save(departmentStock));
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
}
