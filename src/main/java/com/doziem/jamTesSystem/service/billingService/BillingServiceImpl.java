package com.doziem.jamTesSystem.service.billingService;

import com.doziem.jamTesSystem.dto.BillingDto;
import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.model.Billing;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.BillingRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillingServiceImpl implements IBillingService{

    private final BillingRepository billingRepository;
    private final PatientRepository patientRepository;

    public BillingServiceImpl(BillingRepository billingRepository, PatientRepository patientRepository) {
        this.billingRepository = billingRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public BillingDto createBilling(@RequestBody BillingDto billingDto) {
        Patient patient = patientRepository.findById(billingDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Billing billing = BillingDto.mapToEntity(billingDto, patient);
        Billing savedBilling = billingRepository.save(billing);
        return BillingDto.mapToDTO(savedBilling);
    }

    @Override
    public Optional<BillingDto> getBillingById(@PathVariable UUID id) {
        return billingRepository.findById(id).map(BillingDto::mapToDTO);
    }

    @Override
    public List<BillingDto> getAllBillings() {
        return billingRepository.findAll()
                .stream()
                .map(BillingDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingDto> getBillingsByPatientId(@PathVariable UUID patientId) {

        List<Billing> billings = billingRepository.findByPatientId(patientId);

        if (billings.isEmpty()) {
            throw new ResourceNotFoundException("No billings found");
        }

        return billings.stream()
                .map(BillingDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BillingDto updateBilling(@PathVariable UUID id,@RequestBody BillingDto billingDto) {
        return billingRepository.findById(id).map(billing -> {
            billing.setPaid(Boolean.TRUE.equals(billingDto.isPaid()));
            billing.setTotalAmount(billingDto.getTotalAmount() != null ? billingDto.getTotalAmount() : billing.getTotalAmount());
            billing.setPaymentMethod(billingDto.getPaymentMethod() != null ? billingDto.getPaymentMethod() : billing.getPaymentMethod());
            billing.setBillingDate(billingDto.getBillingDate() != null ? billingDto.getBillingDate() : billing.getBillingDate());
            return BillingDto.mapToDTO(billingRepository.save(billing));
        }).orElseThrow(() -> new ResourceNotFoundException("Billing record not found"));
    }

//    private Patient updateExistingPatient(Patient existingPatient, PatientDto patientDto) {
//
//        existingPatient.setFirstName(patientDto.getFirstName() != null? patientDto.getFirstName(): existingPatient.getLastName());
//        existingPatient.setLastName(patientDto.getLastName() != null ? patientDto.getLastName() : existingPatient.getLastName());
//        existingPatient.setEmail(patientDto.getEmail() != null ? patientDto.getEmail() : existingPatient.getEmail());
//        existingPatient.setPhone(patientDto.getPhone() != null ? patientDto.getPhone() : existingPatient.getPhone());
//        existingPatient.setDateOfBirth(patientDto.getDateOfBirth() != null ? patientDto.getDateOfBirth() : existingPatient.getDateOfBirth());
//        existingPatient.setGender(patientDto.getGender() != null ? patientDto.getGender() : existingPatient.getGender());
//        existingPatient.setAddress(patientDto.getAddress() != null ? patientDto.getAddress() : existingPatient.getAddress());
//        existingPatient.setActive(Boolean.FALSE.equals(patientDto.isActive()));
//        return existingPatient;
//    }
    @Override
    public void deleteBilling(@PathVariable UUID id) {
        billingRepository.deleteById(id);

    }
}
