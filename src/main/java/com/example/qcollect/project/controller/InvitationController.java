package com.example.qcollect.project.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.project.dto.AcceptInvitationRequest;
import com.example.qcollect.project.dto.ProjectMemberResponse;
import com.example.qcollect.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final ProjectService projectService;

    @PostMapping("/{token}/accept")
    public ApiResponse<ProjectMemberResponse> accept(
            @PathVariable String token,
            @RequestBody(required = false) AcceptInvitationRequest request
    ) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .success(true)
                .message("Invitation accepted successfully.")
                .data(projectService.acceptInvitation(token, request))
                .build();
    }
}
