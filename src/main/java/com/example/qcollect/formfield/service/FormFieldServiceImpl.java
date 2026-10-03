package com.example.qcollect.formfield.service;

import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.formfield.dto.*;
import com.example.qcollect.formfield.entity.FormField;
import com.example.qcollect.formfield.mapper.FormFieldMapper;
import com.example.qcollect.formfield.enums.FieldType;
import com.example.qcollect.formfield.repository.FormFieldRepository;
import com.example.qcollect.formfield.service.FormFieldService;
import com.example.qcollect.project.enums.ProjectRole;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.project.service.ProjectAuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FormFieldServiceImpl implements FormFieldService {

    private final FormRepository formRepository;

    private final FormFieldRepository formFieldRepository;
    private final ObjectMapper objectMapper;
    private final FormFieldMapper formFieldMapper;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ProjectUserRepository projectUserRepository;

    @Override
    public FormFieldResponse createField(
            UUID formId,
            CreateFormFieldRequest request,
            UUID loggedInUserId) {

        Form form = validateProjectAdmin(formId, loggedInUserId);

        FormField field = FormField.builder()
                .form(form)
                .label(request.getLabel())
                .fieldType(parseFieldType(request.getFieldType()))
                .required(request.getRequired())
                .placeholder(request.getPlaceholder())
                .defaultValue(request.getDefaultValue())
                .fieldOrder(request.getFieldOrder())
                .options(request.getOptions())
                .validationJson(request.getValidationJson())
                .build();

        formFieldRepository.save(field);

        return formFieldMapper.toResponse(field);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormFieldResponse> getFields(UUID formId) {

        return formFieldRepository
                .findByForm_IdAndActiveTrueOrderByFieldOrderAsc(formId)
                .stream()
                .map(formFieldMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FormFieldResponse getField(UUID fieldId) {

        FormField field = formFieldRepository
                .findByIdAndActiveTrue(fieldId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Field not found"));

        return formFieldMapper.toResponse(field);
    }

    @Override
    public FormFieldResponse updateField(
            UUID fieldId,
            UpdateFormFieldRequest request,
            UUID loggedInUserId) {

        FormField field = validateFieldAdmin(fieldId, loggedInUserId);

        field.setLabel(request.getLabel());
        field.setFieldType(parseFieldType(request.getFieldType()));
        field.setRequired(request.getRequired());
        field.setPlaceholder(request.getPlaceholder());
        field.setDefaultValue(request.getDefaultValue());
        field.setFieldOrder(request.getFieldOrder());
        field.setOptions(request.getOptions());
        field.setValidationJson(request.getValidationJson());

        field.setUpdatedAt(LocalDateTime.now());
        field.setUpdatedBy(loggedInUserId);

        formFieldRepository.save(field);

        return formFieldMapper.toResponse(field);
    }

    @Override
    public void deleteField(
            UUID fieldId,
            UUID loggedInUserId) {

        FormField field = validateFieldAdmin(fieldId, loggedInUserId);

        field.setActive(false);
        field.setUpdatedAt(LocalDateTime.now());
        field.setUpdatedBy(loggedInUserId);

        formFieldRepository.save(field);
    }

    private Form validateProjectAdmin(
            UUID formId,
            UUID loggedInUserId) {

        Form form = formRepository.findByIdAndActiveTrue(formId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Form not found"));

        projectUserRepository.findMembership(form.getProject().getId(),
                loggedInUserId,
                ProjectRole.PROJECT_ADMIN.name()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Only Project Admin can modify form fields."));

        return form;
    }

    private FormField validateFieldAdmin(
            UUID fieldId,
            UUID loggedInUserId) {

        FormField field = formFieldRepository.findByIdAndActiveTrue(fieldId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Field not found"));

        validateProjectAdmin(
                field.getForm().getId(),
                loggedInUserId
        );

        return field;
    }



    private FieldType parseFieldType(String fieldType) {
        if (fieldType == null || fieldType.isBlank()) {
            return null;
        }
        return FieldType.valueOf(fieldType.trim().toUpperCase());
    }

    @Override
    public void reorderFields(
            UUID formId,
            List<ReorderFieldRequest> request,
            UUID loggedInUserId
    ) {

        validateProjectAdmin(formId, loggedInUserId);

        for (ReorderFieldRequest item : request) {

            FormField field = formFieldRepository
                    .findByIdAndActiveTrue(item.getFieldId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Field not found."));

            if (!field.getForm().getId().equals(formId)) {
                throw new IllegalArgumentException(
                        "Field does not belong to this form."
                );
            }

            field.setFieldOrder(item.getFieldOrder());

            field.setUpdatedBy(loggedInUserId);

            field.setUpdatedAt(LocalDateTime.now());

            formFieldRepository.save(field);
        }
    }
    @Override
    public FormFieldResponse duplicateField(
            UUID fieldId,
            UUID loggedInUserId
    ) {

        FormField field = validateFieldAdmin(
                fieldId,
                loggedInUserId
        );

        Integer nextOrder =
                Math.toIntExact(formFieldRepository.countByForm_Id(
                        field.getForm().getId()
                ) + 1);

        FormField copy = FormField.builder()
                .form(field.getForm())
                .label(field.getLabel() + " (Copy)")
                .fieldType(field.getFieldType())
                .required(field.getRequired())
                .placeholder(field.getPlaceholder())
                .defaultValue(field.getDefaultValue())
                .fieldOrder(nextOrder)
                .options(field.getOptions())
                .validationJson(field.getValidationJson())
                .build();

        formFieldRepository.save(copy);

        return formFieldMapper.toResponse(copy);
    }
    @Override
    public FormFieldResponse toggleFieldStatus(
            UUID fieldId,
            UUID loggedInUserId
    ) {

        FormField field = validateFieldAdmin(
                fieldId,
                loggedInUserId
        );

        field.setActive(!field.getActive());

        field.setUpdatedBy(loggedInUserId);

        field.setUpdatedAt(LocalDateTime.now());

        formFieldRepository.save(field);

        return formFieldMapper.toResponse(field);
    }
    @Override
    public FormFieldResponse updateValidation(
            UUID fieldId,
            FieldValidationRequest request,
            UUID loggedInUserId
    ) {

        FormField field = validateFieldAdmin(
                fieldId,
                loggedInUserId
        );

        String json = String.format(
                """
                {
                  "minLength":%s,
                  "maxLength":%s,
                  "minValue":%s,
                  "maxValue":%s,
                  "regex":"%s",
                  "message":"%s"
                }
                """,
                request.getMinLength(),
                request.getMaxLength(),
                request.getMinValue(),
                request.getMaxValue(),
                request.getRegex(),
                request.getValidationMessage()
        );

        field.setValidationJson(json);

        field.setUpdatedBy(loggedInUserId);

        field.setUpdatedAt(java.time.LocalDateTime.now());

        formFieldRepository.save(field);

        return formFieldMapper.toResponse(field);
    }
    @Override
    public FormFieldResponse updateOptions(
            UUID fieldId,
            FieldOptionsRequest request,
            UUID loggedInUserId
    ) {

        FormField field = validateFieldAdmin(
                fieldId,
                loggedInUserId
        );

        try {

            field.setOptions(
                    objectMapper.writeValueAsString(
                            request.getOptions()
                    )
            );

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Unable to save options."
            );

        }

        field.setUpdatedBy(loggedInUserId);

        field.setUpdatedAt(LocalDateTime.now());

        formFieldRepository.save(field);

        return formFieldMapper.toResponse(field);

    }
    @Override
    public void copyFields(
            UUID destinationFormId,
            CopyFieldsRequest request,
            UUID loggedInUserId
    ) {

        Form destination =
                validateProjectAdmin(
                        destinationFormId,
                        loggedInUserId
                );

        Form source =
                formRepository
                        .findByIdAndActiveTrue(
                                request.getSourceFormId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Source form not found."
                                )
                        );

        List<FormField> fields =
                formFieldRepository
                        .findByForm_IdAndActiveTrueOrderByFieldOrderAsc(
                                source.getId()
                        );

        for (FormField field : fields) {

            FormField copy =
                    FormField.builder()
                            .form(destination)
                            .label(field.getLabel())
                            .fieldType(field.getFieldType())
                            .required(field.getRequired())
                            .placeholder(field.getPlaceholder())
                            .defaultValue(field.getDefaultValue())
                            .fieldOrder(field.getFieldOrder())
                            .options(field.getOptions())
                            .validationJson(field.getValidationJson())
                            .build();

            formFieldRepository.save(copy);

        }

    }
}
