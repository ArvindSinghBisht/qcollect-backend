package com.example.qcollect.qcdelegation.repository;


import com.example.qcollect.qcdelegation.entity.QcDelegation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QcDelegationRepository
        extends JpaRepository<QcDelegation, UUID> {

    List<QcDelegation> findByProject_IdAndActiveTrue(
            UUID projectId
    );

    Optional<QcDelegation> findByForm_IdAndAccessor_IdAndActiveTrue(
            UUID formId,
            UUID accessorId
    );

    List<QcDelegation> findByDelegateQc_IdAndActiveTrue(
            UUID delegateQcId
    );

    List<QcDelegation> findByOriginalQc_IdAndActiveTrue(
            UUID originalQcId
    );

    Optional<QcDelegation>
    findByForm_IdAndAccessor_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID formId,
            UUID accessorId,
            LocalDate today1,
            LocalDate today2
    );
}