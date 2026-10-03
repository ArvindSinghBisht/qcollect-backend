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
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.qcollect.project.mapper.ProjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
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
    private final PasswordEncoder passwordEncoder;
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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }

        Role systemUserRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new ResourceNotFoundException("USER role not found."));

        User projectAdmin = new User();

        projectAdmin.setTenantId(project.getTenantId());
        projectAdmin.setSystemRole(systemUserRole);
        projectAdmin.setFirstName(request.getFirstName());
        projectAdmin.setLastName(request.getLastName());
        projectAdmin.setEmail(request.getEmail());
        projectAdmin.setPhone(request.getPhone());
        projectAdmin.setPassword(
                passwordEncoder.encode(request.getPassword()));
        projectAdmin.setActive(true);

        projectAdmin = userRepository.save(projectAdmin);

        Role projectAdminRole = roleRepository.findByName(
                        RoleType.PROJECT_ADMIN.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "PROJECT_ADMIN role not found."));

        ProjectUser projectUser = new ProjectUser();

        projectUser.setProjectId(projectId);
        projectUser.setUserId(projectAdmin.getId());
        projectUser.setRoleId(projectAdminRole.getId());

        projectUserRepository.save(projectUser);

        auditHelper.log(
                tenantAdmin.getId(),
                "PROJECT",
                "CREATE_PROJECT_ADMIN",
                project.getId(),
                "Created Project Admin : " + projectAdmin.getEmail(),
                "SYSTEM");

        notificationHelper.notifyUser(
                projectAdmin.getId(),
                "Project Admin Account Created",
                "You have been assigned as Project Admin for project : "
                        + project.getName(),
                NotificationType.PROJECT);
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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }

        Role systemUserRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new ResourceNotFoundException("USER role not found."));

        User accessor = new User();

        accessor.setTenantId(project.getTenantId());
        accessor.setSystemRole(systemUserRole);
        accessor.setFirstName(request.getFirstName());
        accessor.setLastName(request.getLastName());
        accessor.setEmail(request.getEmail());
        accessor.setPhone(request.getPhone());
        accessor.setPassword(
                passwordEncoder.encode(request.getPassword()));
        accessor.setActive(true);

        accessor = userRepository.save(accessor);

        Role accessorRole = roleRepository.findByName(
                        RoleType.ACCESSOR.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException("ACCESSOR role not found."));

        ProjectUser projectUser = new ProjectUser();

        projectUser.setProjectId(projectId);
        projectUser.setUserId(accessor.getId());
        projectUser.setRoleId(accessorRole.getId());

        projectUserRepository.save(projectUser);

        auditHelper.log(
                loggedInUserId,
                "PROJECT",
                "CREATE_ACCESSOR",
                projectId,
                "Created Accessor : " + accessor.getEmail(),
                "SYSTEM");

        notificationHelper.notifyUser(
                accessor.getId(),
                "Accessor Account Created",
                "You have been assigned as Accessor for project : "
                        + project.getName(),
                NotificationType.PROJECT);
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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }

        Role systemUserRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new ResourceNotFoundException("USER role not found."));

        User qc = new User();

        qc.setTenantId(project.getTenantId());
        qc.setSystemRole(systemUserRole);
        qc.setFirstName(request.getFirstName());
        qc.setLastName(request.getLastName());
        qc.setEmail(request.getEmail());
        qc.setPhone(request.getPhone());
        qc.setPassword(
                passwordEncoder.encode(request.getPassword()));
        qc.setActive(true);

        qc = userRepository.save(qc);

        Role qcRole = roleRepository.findByName(
                        RoleType.QUALITY_CHECKER.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException("QUALITY_CHECKER role not found."));

        ProjectUser projectUser = new ProjectUser();

        projectUser.setProjectId(projectId);
        projectUser.setUserId(qc.getId());
        projectUser.setRoleId(qcRole.getId());

        projectUserRepository.save(projectUser);

        auditHelper.log(
                loggedInUserId,
                "PROJECT",
                "CREATE_QUALITY_CHECKER",
                projectId,
                "Created Quality Checker : " + qc.getEmail(),
                "SYSTEM");

        notificationHelper.notifyUser(
                qc.getId(),
                "Quality Checker Account Created",
                "You have been assigned as Quality Checker for project : "
                        + project.getName(),
                NotificationType.PROJECT);
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

        if (projectUserRepository.existsByProjectIdAndUserId(
                projectId,
                user.getId())) {

            throw new BadRequestException(
                    "User already assigned.");

        }

        ProjectUser projectUser = new ProjectUser();

        projectUser.setProjectId(projectId);

        projectUser.setUserId(user.getId());

        projectUser.setRoleId(role.getId());

        projectUserRepository.save(projectUser);

        notificationHelper.notifyUser(

                user.getId(),

                "Project Assignment",

                "You have been added to project : "
                        + project.getName(),

                NotificationType.PROJECT
        );

        return ProjectMemberResponse.builder()

                .id(projectUser.getId())

                .userId(user.getId())

                .userName(
                        user.getFirstName()
                                + " "
                                + user.getLastName())

                .email(user.getEmail())

                .role(role.getName())

                .build();
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

        if (projectInvitationRepository.existsByEmailAndProjectId(
                request.getEmail(),
                projectId)) {

            throw new BadRequestException(
                    "Invitation already sent.");
        }

        String token = UUID.randomUUID().toString();

        ProjectInvitation invitation = new ProjectInvitation();

        invitation.setProject(project);
        invitation.setEmail(request.getEmail());
        invitation.setRole(role);
        invitation.setToken(token);
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        projectInvitationRepository.save(invitation);

        emailService.sendProjectInvitation(
                request.getEmail(),
                project.getName(),
                token
        );

        auditHelper.log(
                loggedInUserId,
                "PROJECT",
                "INVITE_MEMBER",
                projectId,
                "Invitation sent to " + request.getEmail(),
                "SYSTEM"
        );
    }
}