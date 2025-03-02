package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class PatienceService implements IPatientService{
    private final PatientRepository patientRepository;

    public PatienceService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    // Create a new patient
    public PatientDto createPatient(PatientDto patientDto) {
        Patient patient = PatientDto.mapToEntity(patientDto, new Patient());
        return PatientDto.mapToDTO(patientRepository.save(patient));
    }

    // Retrieve a patient by ID
    @Override
    public PatientDto getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(PatientDto::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }


    // Retrieve all patients
    @Override
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientDto::mapToDTO)
                .collect(Collectors.toList());
    }

    // Update a patient
    @Override
    public PatientDto updatePatient(UUID id, PatientDto patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Patient updatedPatient = patientRepository.save(updateExistingPatient(existingPatient,patientDTO));
        return PatientDto.mapToDTO(updatedPatient);
    }

    private Patient updateExistingPatient(Patient existingPatient,PatientDto patientDto) {

        existingPatient.setFirstName(patientDto.getFirstName() != null? patientDto.getFirstName(): existingPatient.getLastName());
        existingPatient.setLastName(patientDto.getLastName() != null ? patientDto.getLastName() : existingPatient.getLastName());
        existingPatient.setEmail(patientDto.getEmail() != null ? patientDto.getEmail() : existingPatient.getEmail());
        existingPatient.setPhone(patientDto.getPhone() != null ? patientDto.getPhone() : existingPatient.getPhone());
        existingPatient.setDateOfBirth(patientDto.getDateOfBirth() != null ? patientDto.getDateOfBirth() : existingPatient.getDateOfBirth());
        existingPatient.setGender(patientDto.getGender() != null ? patientDto.getGender() : existingPatient.getGender());
        existingPatient.setAddress(patientDto.getAddress() != null ? patientDto.getAddress() : existingPatient.getAddress());
        existingPatient.setActive(Boolean.FALSE.equals(patientDto.isActive()));
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
    public void deletePatient(UUID id) {
      Patient patient=  patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);

    }
}
