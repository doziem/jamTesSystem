package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.LabRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LabRequestRepository extends JpaRepository<LabRequest, UUID> {
}
