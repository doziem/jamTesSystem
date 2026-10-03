package com.doziem.jamTesSystem.service.medication;

import com.doziem.jamTesSystem.dto.MedicationDto;
import com.doziem.jamTesSystem.mapper.MedicationMapper;
import com.doziem.jamTesSystem.model.Medication;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.repository.MedicationRepository;
import com.doziem.jamTesSystem.repository.PharmacyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class MedicationService implements IMedicationService {

   private final MedicationRepository medicationRepository;
   private final PharmacyRepository pharmacyRepository;

//   logger for the class
   private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MedicationService.class);

    /**
     * Retrieves all medications.
     *
     * @return a list of MedicationDto
     */
    @Override
    public List<MedicationDto> getAllMedications() {
        return medicationRepository.findAll()
                .stream()
                .map(MedicationMapper ::toDto)
                .toList();
    }
    /**
     * Retrieves a paginated list of medications.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a list of MedicationDto for the specified page
     */
    @Override
    public List<MedicationDto> getAllMedications(int page, int size) {
        return medicationRepository.findAll()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(MedicationMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a medication by its ID.
     *
     * @param id the ID of the medication
     * @return an Optional containing the MedicationDto if found, or empty if not found
     */
    @Override
    public Optional<MedicationDto> getMedicationById(String id) {
//        Use try-catch block to handle any exceptions that may occur during the retrieval process
        try {
            return medicationRepository.findById(id)
                    .map(MedicationMapper::toDto);
        } catch (Exception e) {
            // Log the exception or handle it as needed
            logger.error("Error occurred while retrieving medication with id: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Saves a new medication to the repository.
     *
     * @param medicationDto the MedicationDto to save
     * @param supervisorId the ID of the supervisor attempting to save the medication
     * @return the saved MedicationDto
     */
    @Override
    public MedicationDto saveMedication(MedicationDto medicationDto, String supervisorId) {
        try {
            // Validate that supervisorId is not null or empty and he belongs to the pharmacy
            if (supervisorId == null || supervisorId.isEmpty()) {
                throw new IllegalArgumentException("Supervisor ID is required to save medication");
            }
            // Additional validation to check if the supervisor belongs to the pharmacy can be added here

//            Optional<Pharmacy> pharmacy = pharmacyRepository.findBySupervisorId(supervisorId);
//            if (pharmacy.isEmpty()) {
//                throw new RuntimeException("Supervisor does not belong to any pharmacy");
//            }

            Medication medication = MedicationMapper.toEntity(medicationDto);
            Medication savedMedication = medicationRepository.save(medication);
            return MedicationMapper.toDto(savedMedication);
        } catch (Exception e) {
            logger.error("Error occurred while saving medication: {}", medicationDto, e);
            throw new RuntimeException("Failed to save medication", e);
        }
    }
    /**
     * Updates an existing medication by its ID.
     *
     * @param id the ID of the medication to update
     * @param medicationDto the updated medication data
     * @return the updated MedicationDto
     * @throws RuntimeException if the medication with the specified ID does not exist
     */
    @Override
    public MedicationDto updateMedication(String id, MedicationDto medicationDto) {
        return medicationRepository.findById(id)
                .map(existingMedication -> {
                    existingMedication.setName(medicationDto.getName());
                    existingMedication.setCategory(medicationDto.getCategory());
                    existingMedication.setUnitPrice(medicationDto.getUnitPrice());
                    existingMedication.setActive(medicationDto.isActive());
                    Medication updatedMedication = medicationRepository.save(existingMedication);
                    return MedicationMapper.toDto(updatedMedication);
                })
                .orElseThrow(() -> new RuntimeException("Medication not found with id: " + id));
    }
    /**
     * Deletes a medication by its ID.
     *
     * @param id the ID of the medication to delete
     * @throws RuntimeException if the medication with the specified ID does not exist
     */

    @Override
    public void deleteMedication(String id) {
        //validate if the medication exists before deleting
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found with id: " + id));
        medicationRepository.delete(medication);
    }
}
