package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.BillingMapper;
import com.doziem.jamTesSystem.mapper.LabReportMapper;
import com.doziem.jamTesSystem.mapper.PatientMapper;
import com.doziem.jamTesSystem.mapper.PrescriptionMapper;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PatienceService implements IPatientService{
    private final PatientRepository patientRepository;
    private PatientMapper patientMapper = new PatientMapper(
            new BillingMapper(),
            new LabReportMapper(),
            new PrescriptionMapper());

    public PatienceService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Autowired
    public PatienceService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper != null ? patientMapper : this.patientMapper;
    }

    @Override
    // Create a new patient
    public PatientDto createPatient(PatientDto patientDto) {
        Patient patient = patientMapper.toEntity(patientDto, new Patient());
        return patientMapper.toDto(patientRepository.save(patient));
    }

    // Retrieve a patient by ID
    @Override
    public PatientDto getPatientById(String id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }


    // Retrieve all patients
    @Override
    public List<PatientDto> getAllPatients(int page, int size) {
        //initialize the page and size to default values if they are not provided
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        return patientRepository.findAll()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }

    // Update a patient
    @Override
    public PatientDto updatePatient(String id, PatientDto patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Patient updatedPatient = patientRepository.save(updateExistingPatient(existingPatient,patientDTO));
        return patientMapper.toDto(updatedPatient);
    }

    private Patient updateExistingPatient(Patient existingPatient,PatientDto patientDto) {

        existingPatient.setFirstName(patientDto.getFirstName() != null ? patientDto.getFirstName() : existingPatient.getFirstName());
        existingPatient.setLastName(patientDto.getLastName() != null ? patientDto.getLastName() : existingPatient.getLastName());
        existingPatient.setEmail(patientDto.getEmail() != null ? patientDto.getEmail() : existingPatient.getEmail());
        existingPatient.setPhone(patientDto.getPhone() != null ? patientDto.getPhone() : existingPatient.getPhone());
        existingPatient.setDateOfBirth(patientDto.getDateOfBirth() != null ? patientDto.getDateOfBirth() : existingPatient.getDateOfBirth());
        existingPatient.setGender(patientDto.getGender() != null ? patientDto.getGender() : existingPatient.getGender());
        existingPatient.setAddress(patientDto.getAddress() != null ? patientDto.getAddress() : existingPatient.getAddress());
        existingPatient.setActive(patientDto.isActive());
        return existingPatient;
    }

//    private StockDto updateExistingStockFrom(Stock existingStock, StockDto request) {
//
//        existingStock.setName(request.getName() != null ? request.getName() : existingStock.getName());
//        existingStock.setBuyPrice(request.getBuyPrice() != null ? request.getBuyPrice() : existingStock.getBuyPrice());
//        existingStock.setTicker(request.getTicker() != null ? request.getTicker() : existingStock.getTicker());
//        existingStock.setQuantity(request.getQuantity() != null ? request.getQuantity() : existingStock.getQuantity());
//        existingStock.setVolume(request.getVolume() != null ? request.getVolume() : existingStock.getVolume());
//
//        // Handle portfolio updates
//        if (request.getPortfolio() != null) {
//            String portfolioName = request.getPortfolio().getName().trim();
//            Portfolio portfolio = portfolioRepository.findByName(portfolioName)
//                    .orElseGet(() -> {
//                        Portfolio newPortfolio = new Portfolio();
//                        newPortfolio.setName(portfolioName);
//                        return portfolioRepository.save(newPortfolio); // Save only when creating a new one
//                    });
//            existingStock.setPortfolio(portfolio);
//        }
//
//        Stock updatedStock = stockRepository.save(existingStock);
//        return StockDto.fromStockEntity(updatedStock);
//    }


    @Override
    public void deletePatient(String id) {
      Patient patient=  patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);

    }
}
