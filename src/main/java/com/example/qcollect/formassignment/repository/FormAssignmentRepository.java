package com.example.qcollect.formassignment.repository;



import com.example.qcollect.form.entity.Form;
import com.example.qcollect.formassignment.entity.FormAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormAssignmentRepository
        extends JpaRepository<FormAssignment, UUID> {

    /*
     * Check duplicate assignment
     */
    boolean existsByForm_IdAndAccessor_IdAndActiveTrue(
            UUID formId,
            UUID accessorId
    );

    /*
     * Find assignment for one accessor + form
     */
    Optional<FormAssignment> findByForm_IdAndAccessor_IdAndActiveTrue(
            UUID formId,
            UUID accessorId
    );

    /*
     * All assignments of a form
     */
    List<FormAssignment> findByForm_IdAndActiveTrue(
            UUID formId
    );

    /*
     * All forms assigned to an accessor
     */
    List<FormAssignment> findByAccessor_IdAndActiveTrue(
            UUID accessorId
    );

    /*
     * All assessors assigned to one QC
     */
    List<FormAssignment> findByQualityChecker_IdAndActiveTrue(
            UUID qualityCheckerId
    );

    /*
     * All assignments inside project
     */
    List<FormAssignment> findByProject_IdAndActiveTrue(
            UUID projectId
    );

    /*
     * Remove assignment
     */
    Optional<FormAssignment> findByIdAndActiveTrue(
            UUID assignmentId
    );
    @Query("""
    SELECT fa.form
    FROM FormAssignment fa
    WHERE fa.accessor.id = :accessorId
      AND fa.active = true
      AND fa.form.active = true
""")
    List<Form> findAssignedForms(
            @Param("accessorId") UUID accessorId
    );

}