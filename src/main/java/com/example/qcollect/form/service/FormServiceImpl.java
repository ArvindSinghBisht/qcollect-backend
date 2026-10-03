package com.example.qcollect.form.service;

import com.example.qcollect.audit.service.AuditHelper;
import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.form.dto.*;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.form.mapper.FormMapper;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.formassignment.repository.FormAssignmentRepository;
import com.example.qcollect.formversion.entity.FormVersion;
import com.example.qcollect.formversion.repository.FormVersionRepository;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.project.repository.ProjectRepository;
import com.example.qcollect.project.service.ProjectAuthorizationService;
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
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;
    private final FormVersionRepository formVersionRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final FormMapper formMapper;
    private final AuditHelper auditHelper;
    private final FormAssignmentRepository formAssignmentRepository;
    @Override
    public FormResponse createForm(
            UUID projectId,
            CreateFormRequest request,
            UUID loggedInUserId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Project not found"));

        projectAuthorizationService.requireProjectAdmin(
                projectId,
                loggedInUserId
        );

        Form form = Form.builder()
                .id(UUID.randomUUID())
                .project(project)
                .name(request.getName())
                .description(request.getDescription())
                .surveyJson(request.getSurveyJson())
                .version(1)
                .published(false)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(loggedInUserId)
                .updatedBy(loggedInUserId)
                .build();

        Form savedForm = formRepository.saveAndFlush(form);

        FormVersion version = FormVersion.builder()
                .form(savedForm)
                .version(1)
                .surveyJson(
                        request.getSurveyJson() != null && !request.getSurveyJson().isBlank()
                                ? request.getSurveyJson()
                                : "{\"pages\":[{\"name\":\"page1\",\"elements\":[]}]}"
                )
                .published(false)
                .build();

        version.setCreatedBy(loggedInUserId);
        version.setUpdatedBy(loggedInUserId);

        FormVersion savedVersion = formVersionRepository.saveAndFlush(version);

        auditHelper.log(
                loggedInUserId,
                "FORM",
                "CREATE",
                savedForm.getId(),
                "Form created : " + savedForm.getName(),
                "SYSTEM"
        );

        FormResponse response = formMapper.toResponse(savedForm);
        response.setSurveyJson(savedVersion.getSurveyJson());
        response.setVersion(savedVersion.getVersion());
        response.setPublished(savedVersion.getPublished());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormResponse> getForms(UUID projectId) {

        return formRepository
                .findByProject_IdAndActiveTrue(projectId)
                .stream()
                .map(form -> {
                    FormResponse response = formMapper.toResponse(form);

                    formVersionRepository
                            .findTopByForm_IdOrderByVersionDesc(form.getId())
                            .ifPresent(v -> {
                                response.setSurveyJson(v.getSurveyJson());
                                response.setVersion(v.getVersion());
                                response.setPublished(v.getPublished());
                            });

                    return response;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FormResponse getForm(UUID formId) {

        Form form = formRepository.findByIdAndActiveTrue(formId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Form not found"));

        FormVersion version = formVersionRepository
                .findTopByForm_IdOrderByVersionDesc(form.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Form version not found"));

        FormResponse response = formMapper.toResponse(form);
        response.setSurveyJson(version.getSurveyJson());
        response.setVersion(version.getVersion());
        response.setPublished(version.getPublished());

        return response;
    }

    @Override
    public FormResponse updateForm(
            UUID formId,
            UpdateFormRequest request,
            UUID loggedInUserId) {

        Form form = validateProjectAdminByForm(
                formId,
                loggedInUserId
        );

        form.setName(request.getName());
        form.setDescription(request.getDescription());
        form.setSurveyJson(request.getSurveyJson());

        form.setUpdatedAt(LocalDateTime.now());
        form.setUpdatedBy(loggedInUserId);

        formRepository.save(form);

        auditHelper.log(
                loggedInUserId,
                "FORM",
                "UPDATE",
                form.getId(),
                "Form updated : " + form.getName(),
                "SYSTEM"
        );

        FormResponse response = formMapper.toResponse(form);

        response.setSurveyJson(form.getSurveyJson());
        response.setVersion(form.getVersion());
        response.setPublished(form.getPublished());

        return response;
    }

    @Override
    public void deleteForm(
            UUID formId,
            UUID loggedInUserId) {

        Form form = validateProjectAdminByForm(
                formId,
                loggedInUserId);

        form.setActive(false);
        form.setUpdatedAt(LocalDateTime.now());
        form.setUpdatedBy(loggedInUserId);

        formRepository.save(form);

        auditHelper.log(
                loggedInUserId,
                "FORM",
                "DELETE",
                form.getId(),
                "Form deleted : " + form.getName(),
                "SYSTEM");
    }

    @Override
    @Transactional(readOnly = true)
    public FormPreviewResponse previewForm(UUID formId) {

        Form form = formRepository.findByIdAndActiveTrue(formId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Form not found"));

        FormVersion version = formVersionRepository
                .findTopByForm_IdOrderByVersionDesc(form.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Form version not found"));

        return FormPreviewResponse.builder()
                .id(form.getId())
                .name(form.getName())
                .description(form.getDescription())
                .surveyJson(version.getSurveyJson())
                .version(version.getVersion())
                .published(version.getPublished())
                .build();
    }

//
//    @Override
//    public FormResponse publishForm(
//            UUID formId,
//            UUID loggedInUserId) {
//
//        Form form = validateProjectAdminByForm(
//                formId,
//                loggedInUserId);
//
//        formVersionRepository
//                .findByForm_IdAndPublishedTrue(form.getId())
//                .ifPresent(v -> {
//                    v.setPublished(false);
//                    formVersionRepository.save(v);
//                });
//
//        FormVersion latest = formVersionRepository
//                .findTopByForm_IdOrderByVersionDesc(form.getId())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Form version not found"));
//
//        latest.setPublished(true);
//        formVersionRepository.save(latest);
//
//        form.setUpdatedAt(LocalDateTime.now());
//        form.setUpdatedBy(loggedInUserId);
//        formRepository.save(form);
//
//        auditHelper.log(
//                loggedInUserId,
//                "FORM",
//                "PUBLISH",
//                form.getId(),
//                "Form published",
//                "SYSTEM");
//
//        FormResponse response = formMapper.toResponse(form);
//        response.setSurveyJson(latest.getSurveyJson());
//        response.setVersion(latest.getVersion());
//        response.setPublished(latest.getPublished());
//
//        return response;
//    }

    private Form validateProjectAdminByForm(
            UUID formId,
            UUID loggedInUserId) {

        Form form = formRepository.findByIdAndActiveTrue(formId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Form not found"));

        projectAuthorizationService.requireProjectAdmin(
                form.getProject().getId(),
                loggedInUserId
        );

        return form;
    }

    @Override
    public FormResponse saveDraft(
            UUID formId,
            SaveDraftRequest request,
            UUID loggedInUserId
    ) {

        Form form = validateProjectAdminByForm(
                formId,
                loggedInUserId
        );

        form.setName(request.getName());
        form.setDescription(request.getDescription());

        form.setUpdatedAt(LocalDateTime.now());
        form.setUpdatedBy(loggedInUserId);

        formRepository.save(form);

        FormVersion latest =
                formVersionRepository
                        .findTopByForm_IdOrderByVersionDesc(
                                formId
                        )
                        .orElseThrow();

        FormVersion version =
                FormVersion.builder()
                        .form(form)
                        .version(latest.getVersion() + 1)
                        .surveyJson(request.getSurveyJson())
                        .published(false)
                        .build();

        formVersionRepository.save(version);

        auditHelper.log(
                loggedInUserId,
                "FORM",
                "SAVE_DRAFT",
                formId,
                "Draft saved",
                "SYSTEM"
        );

        FormResponse response = formMapper.toResponse(form);
        response.setSurveyJson(version.getSurveyJson());
        response.setVersion(version.getVersion());
        response.setPublished(version.getPublished());
        return response;
    }

    @Override
    public FormResponse publishForm(
            UUID formId,
            UUID loggedInUserId
    ) {

        Form form = validateProjectAdminByForm(
                formId,
                loggedInUserId
        );

        // Unpublish previous version
        formVersionRepository
                .findByForm_IdAndPublishedTrue(formId)
                .ifPresent(version -> {

                    version.setPublished(false);

                    formVersionRepository.save(version);

                });

        // Publish latest version
        FormVersion latestVersion =
                formVersionRepository
                        .findTopByForm_IdOrderByVersionDesc(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No form version found."
                                ));

        latestVersion.setPublished(true);

        formVersionRepository.save(latestVersion);

        form.setSurveyJson(latestVersion.getSurveyJson());
        form.setVersion(latestVersion.getVersion());
        form.setPublished(true);
        form.setUpdatedAt(LocalDateTime.now());
        form.setUpdatedBy(loggedInUserId);

        formRepository.save(form);

        auditHelper.log(
                loggedInUserId,
                "FORM",
                "PUBLISH",
                form.getId(),
                "Published Version " + latestVersion.getVersion(),
                "SYSTEM"
        );

        FormResponse response = formMapper.toResponse(form);
        response.setSurveyJson(latestVersion.getSurveyJson());
        response.setVersion(latestVersion.getVersion());
        response.setPublished(true);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PublishedFormResponse getPublishedForm(
            UUID formId,
            UUID loggedInUserId
    ) {

        Form form =
                formRepository.findByIdAndActiveTrue(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form not found."
                                ));

        // Check that the user belongs to this project
        projectAuthorizationService.requireProjectMember(
                form.getProject().getId(),
                loggedInUserId
        );

        FormVersion version =
                formVersionRepository
                        .findByForm_IdAndPublishedTrue(formId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Form has not been published."
                                ));

        return PublishedFormResponse.builder()
                .formId(form.getId())
                .formName(form.getName())
                .description(form.getDescription())
                .version(version.getVersion())
                .surveyJson(version.getSurveyJson())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FormBuilderResponse getBuilder(

            UUID formId,

            UUID loggedInUserId

    ) {

        Form form = validateProjectAdminByForm(
                formId,
                loggedInUserId
        );

        FormVersion version = formVersionRepository

                .findTopByForm_IdOrderByVersionDesc(formId)

                .orElse(null);

        return FormBuilderResponse.builder()

                .formId(form.getId())

                .formName(form.getName())

                .version(version != null ? version.getVersion() : 1)

                .published(version != null && version.getPublished())

                .surveyJson(
                        version != null
                                ? version.getSurveyJson()
                                : form.getSurveyJson()
                )

                .build();
    }

    @Override
    public FormBuilderResponse saveBuilder(

            UUID formId,

            String surveyJson,

            UUID loggedInUserId

    ) {

        // Validate Project Admin
        Form form = validateProjectAdminByForm(
                formId,
                loggedInUserId
        );

        // Find existing draft
        FormVersion draft = formVersionRepository
                .findTopByForm_IdAndPublishedFalseOrderByVersionDesc(formId)
                .orElse(null);

        // Create new draft if none exists
        if (draft == null) {

            FormVersion latestPublished = formVersionRepository
                    .findTopByForm_IdOrderByVersionDesc(formId)
                    .orElse(null);

            int versionNumber = latestPublished == null
                    ? 1
                    : latestPublished.getVersion() + 1;

            draft = FormVersion.builder()
                    .form(form)
                    .version(versionNumber)
                    .published(false)
                    .surveyJson(surveyJson)
                    .build();

            draft.setCreatedBy(loggedInUserId);
            draft.setUpdatedBy(loggedInUserId);
        } else {

            draft.setSurveyJson(surveyJson);
            draft.setUpdatedBy(loggedInUserId);
        }

        // Save draft version
        formVersionRepository.save(draft);

        // Update form metadata
        form.setUpdatedAt(LocalDateTime.now());
        form.setUpdatedBy(loggedInUserId);

        formRepository.save(form);

        // Audit
        auditHelper.log(

                loggedInUserId,

                "FORM",

                "SAVE_DRAFT",

                form.getId(),

                "Saved draft version " + draft.getVersion(),

                "SYSTEM"
        );

        // Response
        return FormBuilderResponse.builder()

                .formId(form.getId())

                .formName(form.getName())

                .version(draft.getVersion())

                .published(draft.getPublished())

                .surveyJson(draft.getSurveyJson())

                .build();
    }
    @Override
    public FormBuilderResponse publishBuilder(

            UUID formId,

            UUID loggedInUserId

    ) {

        Form form = validateProjectAdminByForm(
                formId,
                loggedInUserId
        );

        // Find latest draft
        FormVersion draft =
                formVersionRepository
                        .findTopByForm_IdAndPublishedFalseOrderByVersionDesc(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No draft available."
                                ));

        // Unpublish previous version
        formVersionRepository
                .findTopByForm_IdAndPublishedTrueOrderByVersionDesc(formId)
                .ifPresent(version -> {

                    version.setPublished(false);

                    formVersionRepository.save(version);
                });

        // Publish draft
        draft.setPublished(true);

        formVersionRepository.save(draft);

        // Update Form table
        form.setSurveyJson(draft.getSurveyJson());

        form.setVersion(draft.getVersion());

        form.setPublished(true);

        form.setUpdatedAt(LocalDateTime.now());

        form.setUpdatedBy(loggedInUserId);

        formRepository.save(form);

        auditHelper.log(

                loggedInUserId,

                "FORM",

                "PUBLISH",

                form.getId(),

                "Published version " + draft.getVersion(),

                "SYSTEM"
        );

        return FormBuilderResponse.builder()

                .formId(form.getId())

                .formName(form.getName())

                .version(draft.getVersion())

                .published(true)

                .surveyJson(draft.getSurveyJson())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public PublishedFormResponse getPublishedForm(
            UUID formId
    ) {

        Form form =
                formRepository.findByIdAndActiveTrue(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form not found."
                                ));

        FormVersion published =
                formVersionRepository
                        .findTopByForm_IdAndPublishedTrueOrderByVersionDesc(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No published version found."
                                ));

        return PublishedFormResponse.builder()

                .formId(form.getId())

                .formName(form.getName())

                .description(form.getDescription())

                .version(published.getVersion())

                .surveyJson(published.getSurveyJson())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public PublishedSurveyResponse getPublishedSurvey(

            UUID formId,

            UUID loggedInUserId
    ) {

        Form form =
                formRepository
                        .findByIdAndActiveTrue(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form not found."
                                ));

        projectAuthorizationService.requireAccessor(

                form.getProject().getId(),

                loggedInUserId
        );

        FormVersion publishedVersion =
                formVersionRepository

                        .findTopByForm_IdAndPublishedTrueOrderByVersionDesc(
                                formId
                        )

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No published version found."
                                ));

        return PublishedSurveyResponse.builder()

                .formId(form.getId())

                .formName(form.getName())

                .version(publishedVersion.getVersion())

                .surveyJson(publishedVersion.getSurveyJson())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<FormResponse> getAssignedForms(
            UUID accessorId
    ) {

        return formAssignmentRepository

                .findAssignedForms(accessorId)

                .stream()

                .map(form -> {

                    FormResponse response =
                            formMapper.toResponse(form);

                    formVersionRepository

                            .findTopByForm_IdOrderByVersionDesc(
                                    form.getId()
                            )

                            .ifPresent(version -> {

                                response.setSurveyJson(
                                        version.getSurveyJson()
                                );

                                response.setVersion(
                                        version.getVersion()
                                );

                                response.setPublished(
                                        version.getPublished()
                                );

                            });

                    return response;

                })

                .toList();

    }
}