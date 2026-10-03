package com.example.qcollect.formfield.service;

import com.example.qcollect.formfield.dto.*;

import java.util.List;
import java.util.UUID;

public interface FormFieldService {

    FormFieldResponse createField(
            UUID formId,
            CreateFormFieldRequest request,
            UUID loggedInUserId
    );

    List<FormFieldResponse> getFields(UUID formId);

    FormFieldResponse getField(UUID fieldId);

    FormFieldResponse updateField(
            UUID fieldId,
            UpdateFormFieldRequest request,
            UUID loggedInUserId
    );

    void deleteField(
            UUID fieldId,
            UUID loggedInUserId
    );
    void reorderFields(
            UUID formId,
            List<ReorderFieldRequest> request,
            UUID loggedInUserId
    );
    FormFieldResponse duplicateField(
            UUID fieldId,
            UUID loggedInUserId
    );
    FormFieldResponse toggleFieldStatus(
            UUID fieldId,
            UUID loggedInUserId
    );
    FormFieldResponse updateValidation(
            UUID fieldId,
            FieldValidationRequest request,
            UUID loggedInUserId
    );
    FormFieldResponse updateOptions(
            UUID fieldId,
            FieldOptionsRequest request,
            UUID loggedInUserId
    );
    void copyFields(
            UUID destinationFormId,
            CopyFieldsRequest request,
            UUID loggedInUserId
    );
 }