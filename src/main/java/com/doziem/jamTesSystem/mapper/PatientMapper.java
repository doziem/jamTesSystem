package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.BillingDto;
import com.doziem.jamTesSystem.dto.EncounterDto;
import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.model.Patient;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatientMapper {

    private final BillingMapper billingMapper;
    private final LabReportMapper labReportMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final EncounterMapper encounterMapper;

    public PatientMapper(BillingMapper billingMapper, LabReportMapper labReportMapper, PrescriptionMapper prescriptionMapper, EncounterMapper encounterMapper) {
        this.billingMapper = billingMapper;
        this.labReportMapper = labReportMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.encounterMapper = encounterMapper;
    }

    public PatientDto toDto(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getMrn(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getAddress(),
                patient.isActive(),
                mapEncounters(patient),
                mapLabReports(patient),
                mapPrescriptions(patient),
                mapBillingRecords(patient)
        );
    }

    private List<EncounterDto> mapEncounters(Patient patient) {
        if (patient.getEncounters() == null || !Hibernate.isInitialized(patient.getEncounters())) {
            return new ArrayList<>();
        }
        return patient.getEncounters().stream().map(encounterMapper::toDto).toList();
    }

    private List<LabReportDto> mapLabReports(Patient patient) {
        if (patient.getLabReports() == null || !Hibernate.isInitialized(patient.getLabReports())) {
            return new ArrayList<>();
        }
        return patient.getLabReports().stream().map(labReportMapper::toDto).toList();
    }

    private List<PrescriptionDto> mapPrescriptions(Patient patient) {
        if (patient.getPrescriptions() == null || !Hibernate.isInitialized(patient.getPrescriptions())) {
            return new ArrayList<>();
        }
        return patient.getPrescriptions().stream().map(prescriptionMapper::toDto).toList();
    }

    private List<BillingDto> mapBillingRecords(Patient patient) {
        if (patient.getBillingRecords() == null || !Hibernate.isInitialized(patient.getBillingRecords())) {
            return new ArrayList<>();
        }
        return patient.getBillingRecords().stream().map(billingMapper::toDto).toList();
    }

    public Patient toEntity(PatientDto dto, Patient patient) {
        Patient createdPatient = new Patient();
        createdPatient.setId(dto.getId());
        createdPatient.setMrn(dto.getMrn());
        createdPatient.setFirstName(dto.getFirstName());
        createdPatient.setLastName(dto.getLastName());
        createdPatient.setEmail(dto.getEmail());
        createdPatient.setPhone(dto.getPhone());
        createdPatient.setDateOfBirth(dto.getDateOfBirth());
        createdPatient.setGender(dto.getGender());
        createdPatient.setAddress(dto.getAddress());
        createdPatient.setActive(dto.isActive());

        if (dto.getBillingRecords() != null) {
            createdPatient.setBillingRecords(dto.getBillingRecords().stream()
                    .map(billingDto -> billingMapper.toEntity(billingDto, patient != null ? patient : createdPatient))
                    .toList());
        }
        if (dto.getLabReports() != null) {
            createdPatient.setLabReports(dto.getLabReports().stream()
                    .map(labReportDto -> labReportMapper.toEntity(labReportDto, patient != null ? patient : createdPatient, null))
                    .toList());
        }
        if (dto.getPrescriptions() != null) {
            createdPatient.setPrescriptions(dto.getPrescriptions().stream()
                    .map(prescriptionDto -> prescriptionMapper.toEntity(prescriptionDto, patient != null ? patient : createdPatient, null))
                    .toList());
        }

        return createdPatient;
    }
}
