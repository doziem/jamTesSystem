package com.doziem.jamTesSystem.service.billingService;

import com.doziem.jamTesSystem.dto.BillingDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.BillingMapper;
import com.doziem.jamTesSystem.model.Billing;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.BillingRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BillingServiceImpl implements IBillingService{

    private final BillingRepository billingRepository;
    private final PatientRepository patientRepository;
    private final BillingMapper billingMapper;

    public BillingServiceImpl(BillingRepository billingRepository, PatientRepository patientRepository, BillingMapper billingMapper) {
        this.billingRepository = billingRepository;
        this.patientRepository = patientRepository;
        this.billingMapper = billingMapper;
    }

    @Override
    public BillingDto createBilling(@RequestBody BillingDto billingDto) {
        Patient patient = patientRepository.findById(billingDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Billing billing = billingMapper.toEntity(billingDto, patient);
        Billing savedBilling = billingRepository.save(billing);
        return billingMapper.toDto(savedBilling);
    }

    @Override
    public Optional<BillingDto> getBillingById(@PathVariable String id) {
        return billingRepository.findById(id).map(billingMapper::toDto);
    }


    @Override
    public List<BillingDto> getAllBillings(int page, int size) {
        //initialize the page and size to default values if they are not provided
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        return billingRepository.findAll()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(billingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingDto> getBillingsByPatientId(@PathVariable String patientId) {

        List<Billing> billings = billingRepository.findByPatientId(patientId);

        if (billings.isEmpty()) {
            throw new ResourceNotFoundException("No billings found");
        }

        return billings.stream()
                .map(billingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BillingDto updateBilling(@PathVariable String id,@RequestBody BillingDto billingDto) {
        return billingRepository.findById(id).map(billing -> {
            billing.setPaid(Boolean.TRUE.equals(billingDto.isPaid()));
            billing.setTotalAmount(billingDto.getTotalAmount() != null ? billingDto.getTotalAmount() : billing.getTotalAmount());
            billing.setPaymentMethod(billingDto.getPaymentMethod() != null ? billingDto.getPaymentMethod() : billing.getPaymentMethod());
            billing.setBillingDate(billingDto.getBillingDate() != null ? billingDto.getBillingDate() : billing.getBillingDate());
            return billingMapper.toDto(billingRepository.save(billing));
        }).orElseThrow(() -> new ResourceNotFoundException("Billing record not found"));
    }

    @Override
    public ApiResponse deleteBilling(@PathVariable String id) {
        Optional<BillingDto> billingDto = getBillingById(id);

        try {
            if (billingDto.isPresent()) {
                billingRepository.deleteById(id);
                return new ApiResponse(true, "Bill Successfully Deleted");
            }
        } catch (ResourceNotFoundException e) {
          System.out.println("Error Deleting Bill {} "  + e.getMessage());
        }

        return new ApiResponse(false, "Error Deleting Bill");
    }
}
