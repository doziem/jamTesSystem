package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.BillingDto;
import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatientMapper {

    private final BillingMapper billingMapper;
    private final LabReportMapper labReportMapper;
    private final PrescriptionMapper prescriptionMapper;

    public PatientMapper(BillingMapper billingMapper, LabReportMapper labReportMapper, PrescriptionMapper prescriptionMapper) {
        this.billingMapper = billingMapper;
        this.labReportMapper = labReportMapper;
        this.prescriptionMapper = prescriptionMapper;
    }

    public PatientDto toDto(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getAddress(),
                patient.isActive(),
                patient.getLabReports() == null ? new ArrayList<>() : patient.getLabReports().stream().map(labReportMapper::toDto).toList(),
                patient.getPrescriptions() == null ? new ArrayList<>() : patient.getPrescriptions().stream().map(prescriptionMapper::toDto).toList(),
                patient.getBillingRecords() == null ? new ArrayList<>() : patient.getBillingRecords().stream().map(billingMapper::toDto).toList()
        );
    }

    public Patient toEntity(PatientDto dto, Patient patient) {
        Patient createdPatient = new Patient();
        createdPatient.setId(dto.getId());
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
