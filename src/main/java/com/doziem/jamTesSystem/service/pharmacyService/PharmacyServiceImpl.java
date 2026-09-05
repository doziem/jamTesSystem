package com.doziem.jamTesSystem.service.pharmacyService;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.dto.PharmacyDto;
import com.doziem.jamTesSystem.dto.PharmacyInventoryDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.model.*;
import com.doziem.jamTesSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final PatientRepository patientRepository;

    public PharmacyServiceImpl(
            PharmacyRepository pharmacyRepository,
            PharmacyInventoryRepository pharmacyInventoryRepository,
            MedicationRepository medicationRepository,
            PrescriptionRepository prescriptionRepository,
            BillingRepository billingRepository,
            PatientRepository patientRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyInventoryRepository = pharmacyInventoryRepository;
        this.medicationRepository = medicationRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.billingRepository = billingRepository;
        this.patientRepository = patientRepository;
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
        return pharmacyRepository.findAll()
                .stream()
                .map(PharmacyDto::mapToDTO)
                .toList();
    }

    @Override
    public Optional<PharmacyDto> getPharmacyById(String id) {
        return pharmacyRepository.findById(id).map(PharmacyDto::mapToDTO);
    }

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

        Patient patient = patientRepository.findById(prescription.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

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
