package com.example.qcollect.formassignment.service;

//package com.example.qcollect.formassignment.service;

import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.formassignment.dto.AssignFormRequest;
import com.example.qcollect.formassignment.dto.AssignmentResponse;
import com.example.qcollect.formassignment.entity.FormAssignment;
import com.example.qcollect.formassignment.repository.FormAssignmentRepository;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.project.repository.ProjectRepository;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.project.service.ProjectAuthorizationService;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FormAssignmentServiceImpl
        implements FormAssignmentService {

    private final FormAssignmentRepository formAssignmentRepository;

    private final FormRepository formRepository;

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    private final ProjectUserRepository projectUserRepository;

    private final ProjectAuthorizationService
            projectAuthorizationService;

    @Override
    public AssignmentResponse assignForm(

            UUID projectId,

            UUID formId,

            AssignFormRequest request,

            UUID loggedInUserId
    ) {

        /*
         * Only Project Admin
         */
        projectAuthorizationService.requireProjectAdmin(
                projectId,
                loggedInUserId
        );

        /*
         * Project
         */
        Project project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found."
                                ));

        /*
         * Form
         */
        Form form =
                formRepository.findByIdAndActiveTrue(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form not found."
                                ));

        /*
         * Validate Form belongs to Project
         */
        if (!form.getProject().getId().equals(projectId)) {

            throw new BadRequestException(
                    "Form does not belong to this project."
            );

        }

        /*
         * Accessor
         */
        User accessor =
                userRepository.findById(
                                request.getAccessorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Accessor not found."
                                ));

        /*
         * QC
         */
        User qualityChecker =
                userRepository.findById(
                                request.getQualityCheckerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Quality Checker not found."
                                ));

        /*
         * Assigned By
         */
        User assignedBy =
                userRepository.findById(loggedInUserId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                ));

        /*
         * Accessor must belong to Project
         */
        if (!projectUserRepository
                .existsByProjectIdAndUserIdAndActiveTrue(
                        projectId,
                        accessor.getId()
                )) {

            throw new BadRequestException(
                    "Accessor is not a member of this project."
            );

        }

        /*
         * QC must belong to Project
         */
        if (!projectUserRepository
                .existsByProjectIdAndUserIdAndActiveTrue(
                        projectId,
                        qualityChecker.getId()
                )) {

            throw new BadRequestException(
                    "Quality Checker is not a member of this project."
            );

        }

        /*
         * Duplicate Assignment
         */
        if (formAssignmentRepository
                .existsByForm_IdAndAccessor_IdAndActiveTrue(
                        formId,
                        accessor.getId()
                )) {

            throw new BadRequestException(
                    "Accessor already assigned to this form."
            );

        }

        /*
         * Save Assignment
         */
        FormAssignment assignment =
                FormAssignment.builder()

                        .project(project)

                        .form(form)

                        .accessor(accessor)

                        .qualityChecker(qualityChecker)

                        .assignedBy(assignedBy)

                        .active(true)

                        .build();

        formAssignmentRepository.save(assignment);

        return buildResponse(assignment);

    }
    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getFormAssignments(
            UUID formId
    ) {

        Form form =
                formRepository.findByIdAndActiveTrue(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form not found."
                                ));

        return formAssignmentRepository
                .findByForm_IdAndActiveTrue(form.getId())
                .stream()
                .map(this::buildResponse)
                .toList();

    }

    @Override
    public void removeAssignment(

            UUID assignmentId,

            UUID loggedInUserId
    ) {

        FormAssignment assignment =
                formAssignmentRepository
                        .findByIdAndActiveTrue(
                                assignmentId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found."
                                ));

        /*
         * Only Project Admin can remove assignment
         */
        projectAuthorizationService.requireProjectAdmin(

                assignment.getProject().getId(),

                loggedInUserId

        );

        /*
         * Soft Delete
         */
        assignment.setActive(false);

        assignment.setUpdatedAt(LocalDateTime.now());

        formAssignmentRepository.save(
                assignment
        );

    }
    /*
     * ----------------------------------------
     * Helper Method
     * ----------------------------------------
     */
    private AssignmentResponse buildResponse(
            FormAssignment assignment
    ) {

        return AssignmentResponse.builder()

                .assignmentId(
                        assignment.getId()
                )

                .projectId(
                        assignment.getProject().getId()
                )

                .formId(
                        assignment.getForm().getId()
                )

                .formName(
                        assignment.getForm().getName()
                )

                .accessorId(
                        assignment.getAccessor().getId()
                )

                .accessorName(
                        assignment.getAccessor().getFirstName()
                                + " "
                                + assignment.getAccessor().getLastName()
                )

                .qualityCheckerId(
                        assignment.getQualityChecker().getId()
                )

                .qualityCheckerName(
                        assignment.getQualityChecker().getFirstName()
                                + " "
                                + assignment.getQualityChecker().getLastName()
                )

                .assignedBy(
                        assignment.getAssignedBy().getId()
                )

                .assignedByName(
                        assignment.getAssignedBy().getFirstName()
                                + " "
                                + assignment.getAssignedBy().getLastName()
                )

                .assignedAt(
                        assignment.getCreatedAt()
                )

                .active(
                        assignment.getActive()
                )

                .build();

    }

}