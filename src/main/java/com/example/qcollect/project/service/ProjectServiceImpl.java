package com.example.qcollect.project.service;

import com.example.qcollect.audit.service.AuditHelper;
import com.example.qcollect.common.enums.RoleType;
import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.notification.service.NotificationHelper;
import com.example.qcollect.project.dto.*;
import com.example.qcollect.project.dto.request.InviteUserRequest;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.project.entity.ProjectUser;
import com.example.qcollect.project.enums.ProjectRole;
import com.example.qcollect.project.repository.ProjectRepository;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.role.entity.Role;
import com.example.qcollect.role.repository.RoleRepository;
import com.example.qcollect.tenant.entity.Tenant;
import com.example.qcollect.tenant.repository.TenantRepository;
import com.example.qcollect.user.EmailAddress;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.qcollect.project.mapper.ProjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;
import com.example.qcollect.notification.enums.NotificationType;
import com.example.qcollect.project.entity.ProjectInvitation;
import com.example.qcollect.project.repository.ProjectInvitationRepository;
import com.example.qcollect.email.service.EmailService;
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectUserIdentityService projectUserIdentityService;
    private final NotificationHelper notificationHelper;
    private final AuditHelper auditHelper;
    private final ProjectMapper projectMapper;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ProjectInvitationRepository projectInvitationRepository;
    private final EmailService emailService;

    @Override
    public ProjectResponse createProject(

            CreateProjectRequest request, UUID loggedInUserId) {

        User tenantAdmin = userRepository.findById(loggedInUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        if (!"TENANT_ADMIN".equals(
                tenantAdmin.getSystemRole().getName())) {

            throw new BadRequestException(
                    "Only Tenant Admin can create projects.");
        }

        Tenant tenant = tenantRepository.findById(
                        tenantAdmin.getTenantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tenant not found."));

        if (projectRepository.existsByTenantIdAndName(
                tenant.getId(),
                request.getName())) {

            throw new BadRequestException(
                    "Project with same name already exists.");
        }

        Project project = new Project();

        project.setTenantId(tenant.getId());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setActive(true);

        project = projectRepository.save(project);

        auditHelper.log(
                tenantAdmin.getId(),
                "PROJECT",
                "CREATE",
                project.getId(),
                "Project created : " + project.getName(),
                "SYSTEM");

        return projectMapper.toResponse(project);
    }

    @Override
    public void createProjectAdmin(
            UUID projectId,
            CreateProjectAdminRequest request,
            UUID loggedInUserId) {

        User tenantAdmin = userRepository.findById(loggedInUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        if (!RoleType.TENANT_ADMIN.name()
                .equals(tenantAdmin.getSystemRole().getName())) {

            throw new BadRequestException(
                    "Only Tenant Admin can create Project Admin.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        if (!project.getTenantId().equals(tenantAdmin.getTenantId())) {
            throw new BadRequestException(
                    "Project does not belong to your tenant.");
        }

        ProjectUserAccount account = projectUserIdentityService.resolveProjectUser(
                project,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getPassword()
        );
        User projectAdmin = account.user();

        Role projectAdminRole = roleRepository.findByName(
                        RoleType.PROJECT_ADMIN.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "PROJECT_ADMIN role not found."));

        assignMembership(project, projectAdmin, projectAdminRole);

        auditHelper.log(
                tenantAdmin.getId(),
                "PROJECT",
                "CREATE_PROJECT_ADMIN",
                project.getId(),
                membershipAudit("Project Admin", projectAdmin.getEmail(), account.created()),
                "SYSTEM");

        notifyMembership(projectAdmin, project, "Project Admin", account.created());
    }

    @Override
    public void createAccessor(
            UUID projectId,
            CreateAccessorRequest request,
            UUID loggedInUserId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        Role projectAdminRole = roleRepository.findByName(
                        RoleType.PROJECT_ADMIN.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException("PROJECT_ADMIN role not found."));

        if (!projectUserRepository.existsByProjectIdAndUserIdAndRoleIdAndActiveTrue(
                projectId,
                loggedInUserId,
                projectAdminRole.getId())) {

            throw new BadRequestException(
                    "Only Project Admin can create Accessors.");
        }

        ProjectUserAccount account = projectUserIdentityService.resolveProjectUser(
                project,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getPassword()
        );
        User accessor = account.user();

        Role accessorRole = roleRepository.findByName(
                        RoleType.ACCESSOR.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException("ACCESSOR role not found."));

        assignMembership(project, accessor, accessorRole);

        auditHelper.log(
                loggedInUserId,
                "PROJECT",
                "CREATE_ACCESSOR",
                projectId,
                membershipAudit("Accessor", accessor.getEmail(), account.created()),
                "SYSTEM");

        notifyMembership(accessor, project, "Accessor", account.created());
    }

    @Override
    public void createQualityChecker(
            UUID projectId,
            CreateQualityCheckerRequest request,
            UUID loggedInUserId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        Role projectAdminRole = roleRepository.findByName(
                        RoleType.PROJECT_ADMIN.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException("PROJECT_ADMIN role not found."));

        if (!projectUserRepository.existsByProjectIdAndUserIdAndRoleIdAndActiveTrue(
                projectId,
                loggedInUserId,
                projectAdminRole.getId())) {

            throw new BadRequestException(
                    "Only Project Admin can create Quality Checkers.");
        }

        ProjectUserAccount account = projectUserIdentityService.resolveProjectUser(
                project,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getPassword()
        );
        User qc = account.user();

        Role qcRole = roleRepository.findByName(
                        RoleType.QUALITY_CHECKER.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException("QUALITY_CHECKER role not found."));

        assignMembership(project, qc, qcRole);

        auditHelper.log(
                loggedInUserId,
                "PROJECT",
                "CREATE_QUALITY_CHECKER",
                projectId,
                membershipAudit("Quality Checker", qc.getEmail(), account.created()),
                "SYSTEM");

        notifyMembership(qc, project, "Quality Checker", account.created());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(
            UUID projectId,
            UUID loggedInUserId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        requireCanManageMembers(project, loggedInUserId, null);

        List<ProjectUser> members =
                projectUserRepository.findByProjectIdAndActiveTrue(project.getId());

        List<ProjectMemberResponse> response =
                new ArrayList<>();

        for (ProjectUser member : members) {

            User user =
                    userRepository.findById(member.getUserId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found."));

            Role role =
                    roleRepository.findById(member.getRoleId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Role not found."));

            response.add(

                    ProjectMemberResponse.builder()

                            .id(member.getId())

                            .userId(user.getId())

                            .userName(
                                    user.getFirstName()
                                            + " "
                                            + user.getLastName())

                            .email(user.getEmail())

                            .role(role.getName())

                            .build()
            );
        }

        return response;
    }

    @Override
    public void removeMember(
            UUID projectId,
            UUID memberId,
            UUID loggedInUserId
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        requireCanManageMembers(project, loggedInUserId, null);

        ProjectUser projectUser =
                projectUserRepository
                        .findByProjectIdAndUserIdAndActiveTrue(
                                projectId,
                                memberId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project member not found."));

        Role role =
                roleRepository.findById(
                                projectUser.getRoleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found."));

        if (RoleType.PROJECT_ADMIN.name()
                .equals(role.getName())) {

            long adminCount =
                    projectUserRepository
                            .countByProjectIdAndRoleIdAndActiveTrue(
                                    projectId,
                                    role.getId());

            if (adminCount <= 1) {

                throw new BadRequestException(
                        "Project must have at least one Project Admin.");

            }

        }

        if (RoleType.PROJECT_ADMIN.name().equals(role.getName())
                && !isTenantAdminForProject(project, loggedInUserId)) {
            throw new BadRequestException(
                    "Only Tenant Admin can remove a Project Admin."
            );
        }

        projectUserRepository.delete(projectUser);

        auditHelper.log(

                loggedInUserId,

                "PROJECT",

                "REMOVE_MEMBER",

                projectId,

                "Member removed",

                "SYSTEM"
        );
    }

    @Override
    public ProjectMemberResponse addMember(
            UUID projectId,
            AddProjectMemberRequest request,
            UUID loggedInUserId
    ) {

        Project project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found."));

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."));

        if (!project.getTenantId()
                .equals(user.getTenantId())) {

            throw new BadRequestException(
                    "User belongs to another tenant.");

        }

        requireCanManageMembers(
                project,
                loggedInUserId,
                request.getRole()
        );

        Role role =
                roleRepository.findByName(
                                request.getRole().name())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found."));

        ProjectUser projectUser = assignMembership(project, user, role);

        notificationHelper.notifyUser(

                user.getId(),

                "Project Assignment",

                "You have been added to project : "
                        + project.getName(),

                NotificationType.PROJECT
        );

        return toMemberResponse(projectUser, user, role);
    }

    @Override
    public List<ProjectMemberResponse> importMembers(
            UUID projectId,
            List<ImportProjectMemberRequest> rows,
            UUID loggedInUserId
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        if (rows == null || rows.isEmpty()) {
            throw new BadRequestException("Import does not contain any members.");
        }

        Set<String> seenEmails = new HashSet<>();
        for (ImportProjectMemberRequest row : rows) {
            if (row == null || row.getRole() == null) {
                throw new BadRequestException("Each imported member needs a project role.");
            }
            String email = EmailAddress.normalize(row.getEmail());
            if (email == null) {
                throw new BadRequestException("Email is required.");
            }
            if (!seenEmails.add(email)) {
                throw new BadRequestException("Duplicate email in import: " + email);
            }
            requireCanManageMembers(project, loggedInUserId, row.getRole());
        }

        List<ProjectMemberResponse> responses = new ArrayList<>();

        for (ImportProjectMemberRequest row : rows) {
            String email = EmailAddress.normalize(row.getEmail());
            requireCanManageMembers(project, loggedInUserId, row.getRole());

            ProjectUserAccount account = projectUserIdentityService.resolveProjectUser(
                    project,
                    email,
                    row.getFirstName(),
                    row.getLastName(),
                    row.getPhone(),
                    row.getPassword()
            );

            Role role = roleRepository.findByName(row.getRole().name())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Role not found."));

            ProjectUser membership = assignMembership(project, account.user(), role);
            String roleLabel = roleLabel(role.getName());

            auditHelper.log(
                    loggedInUserId,
                    "PROJECT",
                    "IMPORT_MEMBER",
                    projectId,
                    membershipAudit(roleLabel, account.user().getEmail(), account.created()),
                    "SYSTEM"
            );
            notifyMembership(account.user(), project, roleLabel, account.created());
            responses.add(toMemberResponse(membership, account.user(), role));
        }

        return responses;
    }

    @Override
    public ProjectMemberResponse acceptInvitation(
            String token,
            AcceptInvitationRequest request
    ) {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Invitation is invalid.");
        }

        ProjectInvitation invitation = projectInvitationRepository.findByToken(token.trim())
                .orElseThrow(() ->
                        new BadRequestException("Invitation is invalid."));

        if (!"PENDING".equalsIgnoreCase(invitation.getStatus())) {
            throw new BadRequestException("Invitation is no longer pending.");
        }

        if (invitation.getExpiresAt() != null
                && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invitation has expired.");
        }

        Project project = invitation.getProject();
        if (project == null) {
            throw new ResourceNotFoundException("Project not found.");
        }

        Role role = invitation.getRole();
        if (role == null || role.getName() == null) {
            throw new ResourceNotFoundException("Role not found.");
        }

        AcceptInvitationRequest details = request == null
                ? new AcceptInvitationRequest()
                : request;

        ProjectUserAccount account = projectUserIdentityService.resolveProjectUser(
                project,
                invitation.getEmail(),
                details.getFirstName(),
                details.getLastName(),
                details.getPhone(),
                details.getPassword()
        );
        User user = account.user();
        if (account.created()) {
            user.setEmailVerified(true);
            user = userRepository.save(user);
        }

        ProjectUser membership = assignMembership(project, user, role);

        invitation.setStatus("ACCEPTED");
        invitation.setAcceptedAt(LocalDateTime.now());
        invitation.setEmail(user.getEmail());
        projectInvitationRepository.save(invitation);

        String roleLabel = roleLabel(role.getName());
        auditHelper.log(
                user.getId(),
                "PROJECT",
                "ACCEPT_INVITATION",
                project.getId(),
                membershipAudit(roleLabel, user.getEmail(), account.created()),
                "SYSTEM"
        );
        notifyMembership(user, project, roleLabel, account.created());

        return toMemberResponse(membership, user, role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects() {

        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyProjectResponse> getMyProjects(UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found.");
        }

        return projectUserRepository.findActiveMembershipsByUserId(userId)
                .stream()
                .map(membership -> MyProjectResponse.builder()
                        .projectId(membership.getProjectId())
                        .projectName(membership.getProjectName())
                        .roleId(membership.getRoleId())
                        .roleName(membership.getRoleName())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        return projectMapper.toResponse(project);
    }

    private void requireCanManageMembers(
            Project project,
            UUID loggedInUserId,
            ProjectRole targetRole
    ) {
        if (isTenantAdminForProject(project, loggedInUserId)) {
            return;
        }

        if (targetRole == ProjectRole.PROJECT_ADMIN) {
            throw new BadRequestException(
                    "Only Tenant Admin can assign a Project Admin."
            );
        }

        projectAuthorizationService.requireProjectAdmin(
                project.getId(),
                loggedInUserId
        );
    }

    private boolean isTenantAdminForProject(
            Project project,
            UUID userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return RoleType.TENANT_ADMIN.name().equals(
                user.getSystemRole().getName()
        ) && project.getTenantId().equals(user.getTenantId());
    }

    @Override
    public void inviteUser(
            UUID projectId,
            InviteUserRequest request,
            UUID loggedInUserId
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found."));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found."));

        requireCanManageMembers(
                project,
                loggedInUserId,
                ProjectRole.valueOf(role.getName())
        );

        String email = EmailAddress.normalize(request.getEmail());
        if (email == null) {
            throw new BadRequestException("Email is required.");
        }

        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!project.getTenantId().equals(existing.getTenantId())) {
                throw new BadRequestException("User belongs to another tenant.");
            }
            if (projectUserRepository.existsByProjectIdAndUserIdAndActiveTrue(
                    projectId,
                    existing.getId())) {
                throw new BadRequestException(
                        "User is already an active member of this project.");
            }
        });

        if (projectInvitationRepository.existsByEmailAndProjectId(
                email,
                projectId)) {

            throw new BadRequestException(
                    "Invitation already sent.");
        }

        String token = UUID.randomUUID().toString();

        ProjectInvitation invitation = new ProjectInvitation();

        invitation.setProject(project);
        invitation.setEmail(email);
        invitation.setRole(role);
        invitation.setToken(token);
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        projectInvitationRepository.save(invitation);

        emailService.sendProjectInvitation(
                email,
                project.getName(),
                token
        );

        auditHelper.log(
                loggedInUserId,
                "PROJECT",
                "INVITE_MEMBER",
                projectId,
                "Invitation sent to " + email,
                "SYSTEM"
        );
    }

    private ProjectUser assignMembership(Project project, User user, Role role) {
        Optional<ProjectUser> existing = projectUserRepository.findByProjectIdAndUserId(
                project.getId(),
                user.getId()
        );

        if (existing.isPresent()) {
            ProjectUser membership = existing.get();
            if (!Boolean.FALSE.equals(membership.getActive())) {
                throw new BadRequestException(
                        "User is already an active member of this project.");
            }
            membership.setActive(true);
            membership.setRoleId(role.getId());
            return projectUserRepository.save(membership);
        }

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectId(project.getId());
        projectUser.setUserId(user.getId());
        projectUser.setRoleId(role.getId());
        projectUser.setActive(true);
        return projectUserRepository.save(projectUser);
    }

    private void notifyMembership(
            User user,
            Project project,
            String roleLabel,
            boolean accountCreated
    ) {
        if (accountCreated) {
            notificationHelper.notifyUser(
                    user.getId(),
                    roleLabel + " Account Created",
                    "You have been assigned as " + roleLabel + " for project : "
                            + project.getName(),
                    NotificationType.PROJECT
            );
            return;
        }

        notificationHelper.notifyUser(
                user.getId(),
                "Project Assignment",
                "You have been added to project : " + project.getName()
                        + " as " + roleLabel + ".",
                NotificationType.PROJECT
        );
    }

    private String membershipAudit(String roleLabel, String email, boolean accountCreated) {
        if (accountCreated) {
            return "Created " + roleLabel + " : " + email;
        }
        return "Assigned existing user as " + roleLabel + " : " + email;
    }

    private String roleLabel(String roleName) {
        return switch (roleName) {
            case "PROJECT_ADMIN" -> "Project Admin";
            case "ACCESSOR" -> "Accessor";
            case "QUALITY_CHECKER" -> "Quality Checker";
            default -> roleName;
        };
    }

    private ProjectMemberResponse toMemberResponse(
            ProjectUser membership,
            User user,
            Role role
    ) {
        return ProjectMemberResponse.builder()
                .id(membership.getId())
                .userId(user.getId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .role(role.getName())
                .build();
    }
}