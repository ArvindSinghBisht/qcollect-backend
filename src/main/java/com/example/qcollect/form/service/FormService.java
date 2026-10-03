package com.example.qcollect.form.service;

import com.example.qcollect.form.dto.*;

import java.util.List;
import java.util.UUID;
public interface FormService {

    FormResponse createForm(
            UUID projectId,
            CreateFormRequest request,
            UUID loggedInUserId);

    List<FormResponse> getForms(UUID projectId);

    FormResponse getForm(UUID formId);

    FormResponse updateForm(
            UUID formId,
            UpdateFormRequest request,
            UUID loggedInUserId);

    void deleteForm(
            UUID formId,
            UUID loggedInUserId);
    FormPreviewResponse previewForm(
            UUID formId
    );
    FormResponse publishForm(
            UUID formId,
            UUID loggedInUserId
    );

    FormResponse saveDraft(
            UUID formId,
            SaveDraftRequest request,
            UUID loggedInUserId
    );
    PublishedFormResponse getPublishedForm(
            UUID formId,
            UUID loggedInUserId
    );
    FormBuilderResponse getBuilder(
            UUID formId,
            UUID loggedInUserId
    );

    FormBuilderResponse saveBuilder(
            UUID formId,
            String surveyJson,
            UUID loggedInUserId
    );
    FormBuilderResponse publishBuilder(

            UUID formId,

            UUID loggedInUserId

    );
    PublishedFormResponse getPublishedForm(
            UUID formId
    );
    PublishedSurveyResponse getPublishedSurvey(
            UUID formId,
            UUID loggedInUserId
    );
    List<FormResponse> getAssignedForms(
            UUID accessorId
    );
}