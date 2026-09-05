package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, String> {
    Optional<Pharmacy> findByDepartmentAndMainPharmacy(Department department, boolean mainPharmacy);
    List<Pharmacy> findByMainPharmacyRefId(String mainPharmacyId);
    Optional<Pharmacy> findByName(String name);
}
