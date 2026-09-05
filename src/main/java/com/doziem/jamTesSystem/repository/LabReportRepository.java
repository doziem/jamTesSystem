package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.LabReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabReportRepository extends JpaRepository<LabReport, String> {
    List<LabReport> findByLabRequestRequestedById(String requestedBy);

    List<LabReport> findByPatientId(String patientId);
}
