package com.example.qcollect.formversion.service;

import com.example.qcollect.audit.service.AuditHelper;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.formversion.dto.VersionDetailsResponse;
import com.example.qcollect.formversion.dto.VersionResponse;
import com.example.qcollect.formversion.entity.FormVersion;
import com.example.qcollect.formversion.mapper.FormVersionMapper;
import com.example.qcollect.formversion.repository.FormVersionRepository;
import com.example.qcollect.project.service.ProjectAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FormVersionServiceImpl implements FormVersionService {

    private final FormRepository formRepository;

    private final FormVersionRepository formVersionRepository;

    private final ProjectAuthorizationService projectAuthorizationService;

    private final FormVersionMapper mapper;

    private final AuditHelper auditHelper;

    @Override
    @Transactional(readOnly = true)
    public List<VersionResponse> getVersions(UUID formId) {

        formRepository.findByIdAndActiveTrue(formId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Form not found"));

        return formVersionRepository
                .findByForm_IdOrderByVersionDesc(formId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VersionDetailsResponse getVersion(
            UUID formId,
            Integer version
    ) {

        FormVersion entity =
                formVersionRepository
                        .findByForm_IdAndVersion(formId, version)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Version not found"));

        return mapper.toDetailsResponse(entity);
    }

    @Override
    public VersionDetailsResponse publishVersion(
            UUID formId,
            Integer versionNumber,
            UUID loggedInUserId
    ) {

        Form form =
                formRepository.findByIdAndActiveTrue(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form not found"));

        projectAuthorizationService.requireProjectAdmin(
                form.getProject().getId(),
                loggedInUserId
        );

        formVersionRepository
                .findByForm_IdAndPublishedTrue(formId)
                .ifPresent(v -> {

                    v.setPublished(false);

                    formVersionRepository.save(v);
                });

        FormVersion version =
                formVersionRepository
                        .findByForm_IdAndVersion(
                                formId,
                                versionNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Version not found"));

        version.setPublished(true);

        formVersionRepository.save(version);

        auditHelper.log(
                loggedInUserId,
                "FORM_VERSION",
                "PUBLISH",
                version.getId(),
                "Published version " + versionNumber,
                "SYSTEM"
        );

        return mapper.toDetailsResponse(version);
    }

}