package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.LabReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabReportRepository extends JpaRepository<LabReport, UUID> {
    List<LabReport> findByRequestedBy(UUID requestedBy);

    List<LabReport> findByPatientId(UUID patientId);
}
