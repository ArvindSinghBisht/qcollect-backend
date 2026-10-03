package com.example.qcollect.project.service;

import   com.example.qcollect.project.dto.AddProjectMemberRequest;
import com.example.qcollect.project.dto.CreateAccessorRequest;
import com.example.qcollect.project.dto.CreateProjectAdminRequest;
import com.example.qcollect.project.dto.CreateProjectRequest;
import com.example.qcollect.project.dto.CreateQualityCheckerRequest;
import com.example.qcollect.project.dto.MyProjectResponse;
import com.example.qcollect.project.dto.ProjectMemberResponse;
import com.example.qcollect.project.dto.ProjectResponse;

import com.example.qcollect.project.dto.request.InviteUserRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;


public interface ProjectService {


    ProjectResponse createProject(
            CreateProjectRequest request,
            UUID loggedInUserId
    );


    void createProjectAdmin(
            UUID projectId,
            CreateProjectAdminRequest request,
            UUID loggedInTenantAdminId
    );


    void createAccessor(
            UUID projectId,
            CreateAccessorRequest request,
            UUID loggedInUserId
    );


    void createQualityChecker(
            UUID projectId,
            CreateQualityCheckerRequest request,
            UUID loggedInUserId
    );


    List<ProjectMemberResponse> getProjectMembers(
            UUID projectId,
            UUID loggedInUserId
    );


    void removeMember(
            UUID projectId,
            UUID memberId,
            UUID loggedInUserId
    );


    ProjectMemberResponse addMember(
            UUID projectId,
            @Valid AddProjectMemberRequest request,
            UUID loggedInUserId
    );


    // Invite user through Gmail
    void inviteUser(
            UUID projectId,
            @Valid InviteUserRequest request,
            UUID loggedInUserId
    );

    List<ProjectResponse> getProjects();


    List<MyProjectResponse> getMyProjects(
            UUID userId
    );


    ProjectResponse getProject(
            UUID projectId
    );


//    void inviteUser(
//            UUID projectId,
//            InviteUserRequest request,
//            UUID loggedInUserId
//    );
}