package com.example.qcollect.formfield.repository;

import com.example.qcollect.formfield.dto.FieldOptionsRequest;
import com.example.qcollect.formfield.dto.FormFieldResponse;
import com.example.qcollect.formfield.entity.FormField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormFieldRepository
        extends JpaRepository<FormField, UUID> {

    List<FormField> findByForm_IdAndActiveTrueOrderByFieldOrderAsc(UUID formId);

    Optional<FormField> findByIdAndActiveTrue(UUID id);
    long countByForm_Id(UUID formId);

}