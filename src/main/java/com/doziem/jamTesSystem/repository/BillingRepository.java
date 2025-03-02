package com.doziem.jamTesSystem.repository;


import com.doziem.jamTesSystem.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillingRepository extends JpaRepository<Billing, UUID> {
    List<Billing> findByPatientId(UUID patientId);
}
