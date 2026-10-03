package com.example.qcollect.qcdelegation.service;

import com.example.qcollect.form.entity.Form;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.formassignment.entity.FormAssignment;
import com.example.qcollect.formassignment.repository.FormAssignmentRepository;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.project.repository.ProjectRepository;
import com.example.qcollect.project.service.ProjectAuthorizationService;
import com.example.qcollect.qcdelegation.dto.CreateQcDelegationRequest;
import com.example.qcollect.qcdelegation.dto.QcDelegationResponse;
import com.example.qcollect.qcdelegation.entity.QcDelegation;
import com.example.qcollect.qcdelegation.mapper.QcDelegationMapper;
import com.example.qcollect.qcdelegation.repository.QcDelegationRepository;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QcDelegationServiceImpl
        implements QcDelegationService {

    private final QcDelegationRepository delegationRepository;

    private final QcDelegationMapper delegationMapper;

    private final ProjectRepository projectRepository;

    private final FormRepository formRepository;

    private final UserRepository userRepository;

    private final ProjectAuthorizationService authorizationService;
    private final FormAssignmentRepository formAssignmentRepository;

    @Override
    public QcDelegationResponse createDelegation(

            UUID projectId,

            CreateQcDelegationRequest request,

            UUID loggedInUserId
    ) {

        /*
         * Only Project Admin
         */
        authorizationService.requireProjectAdmin(
                projectId,
                loggedInUserId
        );

        /*
         * Load Project
         */
        Project project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"
                                ));

        /*
         * Load Form
         */
        Form form =
                formRepository.findByIdAndActiveTrue(
                        request.getFormId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Form not found"
                        ));

        /*
         * Validate form belongs to project
         */
        if (!form.getProject().getId().equals(projectId)) {

            throw new ResourceNotFoundException(
                    "Form does not belong to project."
            );
        }

        /*
         * Load Accessor
         */
        User accessor =
                userRepository.findById(
                        request.getAccessorId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Accessor not found"
                        ));

        /*
         * Load Delegate QC
         */
        User delegateQc =
                userRepository.findById(
                        request.getDelegateQcId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delegate QC not found"
                        ));

        /*
         * Find current Form Assignment
         */
        FormAssignment assignment =
                formAssignmentRepository
                        .findByForm_IdAndAccessor_IdAndActiveTrue(
                                request.getFormId(),
                                request.getAccessorId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form Assignment not found."
                                ));

        User originalQc =
                assignment.getQualityChecker();

        /*
         * Prevent self delegation
         */
        if (originalQc.getId().equals(delegateQc.getId())) {

            throw new IllegalArgumentException(
                    "Delegate QC cannot be same as Original QC."
            );
        }

        /*
         * Validate Dates
         */
        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End Date cannot be before Start Date."
            );
        }

        /*
         * Prevent duplicate active delegation
         */
        delegationRepository
                .findByForm_IdAndAccessor_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(

                        request.getFormId(),

                        request.getAccessorId(),

                        request.getEndDate(),

                        request.getStartDate()

                )
                .ifPresent(existing -> {

                    throw new IllegalArgumentException(
                            "An active delegation already exists for this Form and Accessor."
                    );

                });

        /*
         * Create Delegation
         */
        QcDelegation delegation =
                QcDelegation.builder()

                        .project(project)

                        .form(form)

                        .accessor(accessor)

                        .originalQc(originalQc)

                        .delegateQc(delegateQc)

                        .startDate(request.getStartDate())

                        .endDate(request.getEndDate())

                        .reason(request.getReason())

                        .active(true)

                        .build();

        delegation.setCreatedBy(loggedInUserId);

        delegation.setUpdatedBy(loggedInUserId);

        delegationRepository.save(delegation);

        return delegationMapper.toResponse(delegation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QcDelegationResponse> getDelegations(
            UUID projectId,
            UUID loggedInUserId
    ) {

        authorizationService.requireProjectAdmin(
                projectId,
                loggedInUserId
        );

        return delegationRepository
                .findByProject_IdAndActiveTrue(projectId)
                .stream()
                .map(delegationMapper::toResponse)
                .toList();
    }

    @Override
    public void removeDelegation(
            UUID delegationId,
            UUID loggedInUserId
    ) {

        QcDelegation delegation =
                delegationRepository.findById(delegationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Delegation not found"
                                ));

        authorizationService.requireProjectAdmin(
                delegation.getProject().getId(),
                loggedInUserId
        );

        delegation.setActive(false);

        delegation.setUpdatedBy(loggedInUserId);

        delegationRepository.save(delegation);
    }
}