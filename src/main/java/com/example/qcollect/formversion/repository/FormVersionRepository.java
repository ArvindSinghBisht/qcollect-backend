package com.example.qcollect.formversion.repository;

import com.example.qcollect.formversion.entity.FormVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormVersionRepository
        extends JpaRepository<FormVersion, UUID> {

    List<FormVersion> findByForm_IdOrderByVersionDesc(
            UUID formId
    );

    Optional<FormVersion> findByForm_IdAndPublishedTrue(
            UUID formId
    );

    Optional<FormVersion> findTopByForm_IdOrderByVersionDesc(
            UUID formId
    );
        Optional<FormVersion> findByForm_IdAndVersion(UUID formId, Integer version);

    Optional<FormVersion> findTopByForm_IdAndPublishedFalseOrderByVersionDesc(
            UUID formId
    );

    Optional<FormVersion> findTopByForm_IdAndPublishedTrueOrderByVersionDesc(
            UUID formId
    );
}