package com.example.qcollect.formassignment.service;


import com.example.qcollect.formassignment.dto.AssignFormRequest;
import com.example.qcollect.formassignment.dto.AssignmentResponse;

import java.util.List;
import java.util.UUID;

public interface FormAssignmentService {

    AssignmentResponse assignForm(
            UUID projectId,
            UUID formId,
            AssignFormRequest request,
            UUID loggedInUserId
    );

    List<AssignmentResponse> getFormAssignments(
            UUID formId
    );

    void removeAssignment(
            UUID assignmentId,
            UUID loggedInUserId
    );

}