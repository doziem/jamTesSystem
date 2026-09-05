package com.doziem.jamTesSystem.repository;


import com.doziem.jamTesSystem.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingRepository extends JpaRepository<Billing, String> {
    List<Billing> findByPatientId(String patientId);
}
