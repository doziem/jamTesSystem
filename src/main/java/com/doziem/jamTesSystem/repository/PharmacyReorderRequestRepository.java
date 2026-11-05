package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.constant.ReorderRequestStatus;
import com.doziem.jamTesSystem.model.PharmacyReorderRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PharmacyReorderRequestRepository extends JpaRepository<PharmacyReorderRequest, String> {
    List<PharmacyReorderRequest> findByRequestedByIdOrderByRequestedAtDesc(String userId);
    List<PharmacyReorderRequest> findAllByOrderByRequestedAtDesc();
    List<PharmacyReorderRequest> findByStatusOrderByRequestedAtAsc(ReorderRequestStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT request FROM PharmacyReorderRequest request WHERE request.id = :id AND request.status = :status")
    Optional<PharmacyReorderRequest> findByIdAndStatusForUpdate(
            @Param("id") String id,
            @Param("status") ReorderRequestStatus status);
}
