package com.example.qcollect.formassignment.controller;



import com.example.qcollect.auth.security.CustomUserDetails;
import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.formassignment.dto.AssignFormRequest;
import com.example.qcollect.formassignment.dto.AssignmentResponse;
import com.example.qcollect.formassignment.service.FormAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/forms")
@RequiredArgsConstructor
public class FormAssignmentController {

    private final FormAssignmentService formAssignmentService;

    /*
     * Assign Accessor + QC to Form
     */
    @PostMapping("/{formId}/assignments")
    public ApiResponse<AssignmentResponse> assignForm(

            @PathVariable UUID formId,

            @RequestParam UUID projectId,

            @Valid
            @RequestBody AssignFormRequest request,

            @AuthenticationPrincipal
            CustomUserDetails user

    ) {

        return ApiResponse.<AssignmentResponse>builder()

                .success(true)

                .message("Assignment created successfully.")

                .data(

                        formAssignmentService.assignForm(

                                projectId,

                                formId,

                                request,

                                user.getUserId()

                        )

                )

                .build();

    }

    /*
     * List assignments of one Form
     */
    @GetMapping("/{formId}/assignments")
    public ApiResponse<List<AssignmentResponse>>
    getAssignments(

            @PathVariable UUID formId

    ) {

        return ApiResponse
                .<List<AssignmentResponse>>builder()

                .success(true)

                .message("Assignments fetched successfully.")

                .data(

                        formAssignmentService
                                .getFormAssignments(formId)

                )

                .build();

    }

    /*
     * Remove Assignment
     */
    @DeleteMapping("/assignments/{assignmentId}")
    public ApiResponse<Void> removeAssignment(

            @PathVariable UUID assignmentId,

            @AuthenticationPrincipal
            CustomUserDetails user

    ) {

        formAssignmentService.removeAssignment(

                assignmentId,

                user.getUserId()

        );

        return ApiResponse.<Void>builder()

                .success(true)

                .message("Assignment removed successfully.")

                .build();

    }

}