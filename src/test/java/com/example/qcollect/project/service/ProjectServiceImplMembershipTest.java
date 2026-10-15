package com.example.qcollect.project.service;

import com.example.qcollect.audit.service.AuditHelper;
import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.email.service.EmailService;
import com.example.qcollect.notification.enums.NotificationType;
import com.example.qcollect.notification.service.NotificationHelper;
import com.example.qcollect.project.dto.AcceptInvitationRequest;
import com.example.qcollect.project.dto.AddProjectMemberRequest;
import com.example.qcollect.project.dto.CreateAccessorRequest;
import com.example.qcollect.project.dto.CreateProjectAdminRequest;
import com.example.qcollect.project.dto.CreateQualityCheckerRequest;
import com.example.qcollect.project.dto.ImportProjectMemberRequest;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.project.entity.ProjectInvitation;
import com.example.qcollect.project.entity.ProjectUser;
import com.example.qcollect.project.enums.ProjectRole;
import com.example.qcollect.project.mapper.ProjectMapper;
import com.example.qcollect.project.repository.ProjectInvitationRepository;
import com.example.qcollect.project.repository.ProjectRepository;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.role.entity.Role;
import com.example.qcollect.role.repository.RoleRepository;
import com.example.qcollect.tenant.repository.TenantRepository;
import com.example.qcollect.user.EmailAddress;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectServiceImplMembershipTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectUserRepository projectUserRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private NotificationHelper notificationHelper;
    @Mock
    private AuditHelper auditHelper;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectAuthorizationService projectAuthorizationService;
    @Mock
    private ProjectInvitationRepository projectInvitationRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private final Map<String, User> usersByEmail = new HashMap<>();
    private final List<ProjectUser> memberships = new ArrayList<>();
    private final Map<String, Role> roles = new HashMap<>();
    private final AtomicInteger userInserts = new AtomicInteger();

    private ProjectServiceImpl service;

    private UUID tenantA;
    private UUID tenantB;
    private Project projectA;
    private Project projectB;
    private Project otherTenantProject;
    private User tenantAdmin;
    private User projectAdmin;

    @BeforeEach
    void setUp() {
        tenantA = UUID.randomUUID();
        tenantB = UUID.randomUUID();
        for (String name : List.of(
                "USER", "TENANT_ADMIN", "PROJECT_ADMIN", "ACCESSOR", "QUALITY_CHECKER")) {
            Role role = new Role();
            role.setId(UUID.randomUUID());
            role.setName(name);
            role.setActive(true);
            roles.put(name, role);
        }

        projectA = project(tenantA, "North survey");
        projectB = project(tenantA, "South survey");
        otherTenantProject = project(tenantB, "Other tenant survey");

        tenantAdmin = store(user("tenant.admin@example.com", tenantA, roles.get("TENANT_ADMIN"), "admin-hash"));
        projectAdmin = store(user("project.admin@example.com", tenantA, roles.get("USER"), "pa-hash"));
        membership(projectA, projectAdmin, "PROJECT_ADMIN");
        membership(projectB, projectAdmin, "PROJECT_ADMIN");

        when(passwordEncoder.encode(any())).thenAnswer(invocation -> "ENC:" + invocation.getArgument(0));
        when(userRepository.findAllByNormalizedEmail(any())).thenAnswer(invocation ->
                matches(invocation.getArgument(0)));
        when(userRepository.findByEmail(any())).thenAnswer(invocation ->
                matches(invocation.getArgument(0)).stream().findFirst());
        when(userRepository.findById(any())).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return usersByEmail.values().stream()
                    .filter(user -> id.equals(user.getId()))
                    .findFirst();
        });
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            userInserts.incrementAndGet();
            return store(invocation.getArgument(0));
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation ->
                store(invocation.getArgument(0)));
        when(roleRepository.findByName(any())).thenAnswer(invocation ->
                Optional.ofNullable(roles.get(invocation.getArgument(0))));
        when(roleRepository.findById(any())).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return roles.values().stream().filter(role -> id.equals(role.getId())).findFirst();
        });
        when(projectRepository.findById(projectA.getId())).thenReturn(Optional.of(projectA));
        when(projectRepository.findById(projectB.getId())).thenReturn(Optional.of(projectB));
        when(projectRepository.findById(otherTenantProject.getId()))
                .thenReturn(Optional.of(otherTenantProject));
        when(projectUserRepository.save(any(ProjectUser.class))).thenAnswer(invocation -> {
            ProjectUser membership = invocation.getArgument(0);
            if (membership.getId() == null) {
                membership.setId(UUID.randomUUID());
            }
            memberships.removeIf(existing ->
                    existing.getProjectId().equals(membership.getProjectId())
                            && existing.getUserId().equals(membership.getUserId()));
            memberships.add(membership);
            return membership;
        });
        when(projectUserRepository.findByProjectIdAndUserId(any(), any())).thenAnswer(invocation ->
                memberships.stream()
                        .filter(membership -> membership.getProjectId().equals(invocation.getArgument(0))
                                && membership.getUserId().equals(invocation.getArgument(1)))
                        .findFirst());
        when(projectUserRepository.existsByProjectIdAndUserIdAndActiveTrue(any(), any()))
                .thenAnswer(invocation -> activeMembership(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        null));
        when(projectUserRepository.existsByProjectIdAndUserIdAndRoleIdAndActiveTrue(any(), any(), any()))
                .thenAnswer(invocation -> activeMembership(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        invocation.getArgument(2)));
        when(projectInvitationRepository.save(any(ProjectInvitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doAnswer(invocation -> {
            UUID projectId = invocation.getArgument(0);
            UUID userId = invocation.getArgument(1);
            if (!activeMembership(projectId, userId, roles.get("PROJECT_ADMIN").getId())) {
                throw new ResourceNotFoundException("Required project role: PROJECT_ADMIN");
            }
            return null;
        }).when(projectAuthorizationService).requireProjectAdmin(any(), any());

        ProjectUserAccountWriter writer = new ProjectUserAccountWriter(userRepository);
        ProjectUserIdentityService identityService = new ProjectUserIdentityService(
                userRepository,
                roleRepository,
                writer,
                passwordEncoder
        );
        service = new ProjectServiceImpl(
                projectRepository,
                projectUserRepository,
                tenantRepository,
                userRepository,
                roleRepository,
                identityService,
                notificationHelper,
                auditHelper,
                projectMapper,
                projectAuthorizationService,
                projectInvitationRepository,
                emailService
        );
    }

    @Test
    void newEmailCreatesOneUserAndOneMembership() {
        service.createAccessor(projectA.getId(), accessor("ada@example.com", "fresh-password"), projectAdmin.getId());

        User created = usersByEmail.get("ada@example.com");
        assertEquals(1, userInserts.get());
        assertEquals("ENC:fresh-password", created.getPassword());
        assertEquals("USER", created.getSystemRole().getName());
        assertEquals(tenantA, created.getTenantId());
        assertFalse(created.getEmailVerified());
        assertEquals(1, membershipsFor(created.getId()).size());
        assertEquals(roles.get("ACCESSOR").getId(), membershipsFor(created.getId()).get(0).getRoleId());
        verify(notificationHelper).notifyUser(
                eq(created.getId()),
                eq("Accessor Account Created"),
                contains("North survey"),
                eq(NotificationType.PROJECT)
        );
    }

    @Test
    void existingSameTenantUserCanBeAccessorInAnotherProject() {
        User existing = accessorOnProjectA();

        service.createAccessor(
                projectB.getId(),
                accessor("  Alice@Example.com ", "should-not-be-used"),
                projectAdmin.getId()
        );

        assertEquals(0, userInserts.get());
        assertEquals(2, membershipsFor(existing.getId()).size());
        assertTrue(membershipsFor(existing.getId()).stream()
                .anyMatch(membership -> membership.getProjectId().equals(projectB.getId())
                        && membership.getRoleId().equals(roles.get("ACCESSOR").getId())));
        verify(notificationHelper).notifyUser(
                eq(existing.getId()),
                eq("Project Assignment"),
                eq("You have been added to project : South survey as Accessor."),
                eq(NotificationType.PROJECT)
        );
    }

    @Test
    void existingSameTenantUserCanBeQualityCheckerInAnotherProject() {
        User existing = accessorOnProjectA();

        service.createQualityChecker(
                projectB.getId(),
                qualityChecker("alice@example.com"),
                projectAdmin.getId()
        );

        assertEquals(0, userInserts.get());
        assertTrue(membershipsFor(existing.getId()).stream()
                .anyMatch(membership -> membership.getProjectId().equals(projectB.getId())
                        && membership.getRoleId().equals(roles.get("QUALITY_CHECKER").getId())));
    }

    @Test
    void tenantAdminCanAssignExistingUserAsProjectAdminInAnotherProject() {
        User existing = accessorOnProjectA();

        service.createProjectAdmin(projectB.getId(), projectAdminRequest("alice@example.com"), tenantAdmin.getId());

        assertEquals(0, userInserts.get());
        assertEquals("USER", existing.getSystemRole().getName());
        assertTrue(membershipsFor(existing.getId()).stream()
                .anyMatch(membership -> membership.getProjectId().equals(projectB.getId())
                        && membership.getRoleId().equals(roles.get("PROJECT_ADMIN").getId())));
        verify(notificationHelper).notifyUser(
                eq(existing.getId()),
                eq("Project Assignment"),
                contains("as Project Admin."),
                eq(NotificationType.PROJECT)
        );
    }

    @Test
    void reusingAccountDoesNotChangePasswordOrSystemRole() {
        User existing = accessorOnProjectA();
        existing.setSystemRole(roles.get("TENANT_ADMIN"));
        Role originalRole = existing.getSystemRole();
        String originalPassword = existing.getPassword();
        String originalPhone = existing.getPhone();
        String originalFirstName = existing.getFirstName();

        service.createAccessor(projectB.getId(), accessor("ALICE@example.com", "replacement"), projectAdmin.getId());

        assertSame(originalRole, existing.getSystemRole());
        assertEquals(originalPassword, existing.getPassword());
        assertEquals(originalPhone, existing.getPhone());
        assertEquals(originalFirstName, existing.getFirstName());
        assertEquals("TENANT_ADMIN", originalRole.getName());
        verify(passwordEncoder, never()).encode(any());
        assertEquals(1, usersByEmail.values().stream()
                .filter(user -> "alice@example.com".equals(user.getEmail()))
                .count());
    }

    @Test
    void sameUserCannotBeAddedTwiceToTheSameProject() {
        User existing = accessorOnProjectA();

        BadRequestException duplicateRole = assertThrows(
                BadRequestException.class,
                () -> service.createQualityChecker(
                        projectA.getId(),
                        qualityChecker("alice@example.com"),
                        projectAdmin.getId()
                )
        );
        AddProjectMemberRequest request = new AddProjectMemberRequest();
        request.setUserId(existing.getId());
        request.setRole(ProjectRole.QUALITY_CHECKER);
        service.addMember(projectB.getId(), request, tenantAdmin.getId());
        BadRequestException duplicateMember = assertThrows(
                BadRequestException.class,
                () -> service.addMember(projectB.getId(), request, tenantAdmin.getId())
        );

        assertEquals("User is already an active member of this project.", duplicateRole.getMessage());
        assertEquals("User is already an active member of this project.", duplicateMember.getMessage());
        assertEquals(1, membershipsFor(existing.getId()).stream()
                .filter(membership -> membership.getProjectId().equals(projectA.getId()))
                .count());
        assertEquals(0, userInserts.get());
    }

    @Test
    void crossTenantUserIsNotReused() {
        User outsider = store(user("outsider@example.com", tenantB, roles.get("USER"), "outsider-hash"));

        BadRequestException fromCreate = assertThrows(
                BadRequestException.class,
                () -> service.createAccessor(
                        projectA.getId(),
                        accessor(" Outsider@Example.com ", "nope"),
                        projectAdmin.getId()
                )
        );
        AddProjectMemberRequest request = new AddProjectMemberRequest();
        request.setUserId(outsider.getId());
        request.setRole(ProjectRole.ACCESSOR);
        BadRequestException fromAdd = assertThrows(
                BadRequestException.class,
                () -> service.addMember(projectA.getId(), request, tenantAdmin.getId())
        );

        assertEquals("User belongs to another tenant.", fromCreate.getMessage());
        assertEquals("User belongs to another tenant.", fromAdd.getMessage());
        assertEquals("outsider-hash", outsider.getPassword());
        assertEquals("USER", outsider.getSystemRole().getName());
        assertTrue(membershipsFor(outsider.getId()).isEmpty());
        assertEquals(0, userInserts.get());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void invalidAuthorizationAndMembershipStillFail() {
        UUID strangerId = UUID.randomUUID();
        store(user("stranger@example.com", tenantA, roles.get("USER"), "stranger-hash"));
        User stranger = usersByEmail.get("stranger@example.com");
        stranger.setId(strangerId);

        BadRequestException notProjectAdmin = assertThrows(
                BadRequestException.class,
                () -> service.createAccessor(
                        projectA.getId(),
                        accessor("new.person@example.com", "secret123"),
                        strangerId
                )
        );
        BadRequestException notTenantAdmin = assertThrows(
                BadRequestException.class,
                () -> service.createProjectAdmin(
                        projectB.getId(),
                        projectAdminRequest("new.person@example.com"),
                        projectAdmin.getId()
                )
        );
        BadRequestException wrongTenantProject = assertThrows(
                BadRequestException.class,
                () -> service.createProjectAdmin(
                        otherTenantProject.getId(),
                        projectAdminRequest("new.person@example.com"),
                        tenantAdmin.getId()
                )
        );
        AddProjectMemberRequest request = new AddProjectMemberRequest();
        request.setUserId(stranger.getId());
        request.setRole(ProjectRole.ACCESSOR);
        ResourceNotFoundException notAllowed = assertThrows(
                ResourceNotFoundException.class,
                () -> service.addMember(projectA.getId(), request, strangerId)
        );

        assertEquals("Only Project Admin can create Accessors.", notProjectAdmin.getMessage());
        assertEquals("Only Tenant Admin can create Project Admin.", notTenantAdmin.getMessage());
        assertEquals("Project does not belong to your tenant.", wrongTenantProject.getMessage());
        assertEquals("Required project role: PROJECT_ADMIN", notAllowed.getMessage());
        assertFalse(usersByEmail.containsKey("new.person@example.com"));
        assertEquals(0, userInserts.get());
    }

    @Test
    void repeatedImportsAndRequestsDoNotCreateDuplicateUsers() {
        ImportProjectMemberRequest first = importRow("Ada@Example.com", ProjectRole.ACCESSOR);
        ImportProjectMemberRequest duplicateInFile = importRow(" ada@example.com ", ProjectRole.QUALITY_CHECKER);
        BadRequestException duplicateFile = assertThrows(
                BadRequestException.class,
                () -> service.importMembers(projectA.getId(), List.of(first, duplicateInFile), projectAdmin.getId())
        );

        service.importMembers(projectA.getId(), List.of(first), projectAdmin.getId());
        ImportProjectMemberRequest secondProject = importRow("ADA@example.com", ProjectRole.QUALITY_CHECKER);
        service.importMembers(projectB.getId(), List.of(secondProject), projectAdmin.getId());
        BadRequestException repeated = assertThrows(
                BadRequestException.class,
                () -> service.importMembers(projectA.getId(), List.of(importRow("ada@example.com", ProjectRole.ACCESSOR)), projectAdmin.getId())
        );

        assertEquals("Duplicate email in import: ada@example.com", duplicateFile.getMessage());
        assertEquals("User is already an active member of this project.", repeated.getMessage());
        assertEquals(1, userInserts.get());
        assertEquals(1, usersByEmail.values().stream()
                .filter(user -> "ada@example.com".equals(user.getEmail()))
                .count());
        User imported = usersByEmail.get("ada@example.com");
        assertEquals(2, membershipsFor(imported.getId()).size());
        assertEquals("ENC:import-password", imported.getPassword());
    }

    @Test
    void emailLookupIgnoresCaseAndWhitespace() {
        User existing = accessorOnProjectA();

        service.createAccessor(
                projectB.getId(),
                accessor("   AlIcE@ExAmPlE.com   ", "ignored"),
                projectAdmin.getId()
        );

        assertEquals(0, userInserts.get());
        assertEquals("alice@example.com", existing.getEmail());
        assertEquals(existing.getId(), membershipsFor(existing.getId()).stream()
                .filter(membership -> membership.getProjectId().equals(projectB.getId()))
                .findFirst()
                .orElseThrow()
                .getUserId());
        assertEquals("alice@example.com", EmailAddress.normalize("   AlIcE@ExAmPlE.com   "));
    }

    @Test
    void paddedEmailPassesValidationAndMatchesExistingAccount() throws Exception {
        User existing = accessorOnProjectA();
        ObjectMapper mapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CreateAccessorRequest request = mapper.readValue(
                """
                {"firstName":"Ada","lastName":"Lovelace","email":"  Alice@Example.com  ","phone":"9876543210","password":"ignored-password"}
                """,
                CreateAccessorRequest.class
        );

        assertTrue(validator.validate(request).isEmpty());
        assertEquals("Alice@Example.com", request.getEmail());
        assertEquals("alice@example.com", EmailAddress.normalize(request.getEmail()));

        service.createAccessor(projectB.getId(), request, projectAdmin.getId());

        assertEquals(0, userInserts.get());
        assertEquals(existing.getId(), membershipsFor(existing.getId()).stream()
                .filter(membership -> membership.getProjectId().equals(projectB.getId()))
                .findFirst()
                .orElseThrow()
                .getUserId());
        assertEquals("kept-hash", existing.getPassword());
        assertEquals("USER", existing.getSystemRole().getName());
    }

    @Test
    void concurrentCreateReusesTheAccountThatWonTheInsert() {
        User raced = user("ada@example.com", tenantA, roles.get("USER"), "original-hash");
        raced.setFirstName("Ada");
        AtomicInteger lookups = new AtomicInteger();
        when(userRepository.findAllByNormalizedEmail(any())).thenAnswer(invocation -> {
            if (!"ada@example.com".equals(invocation.getArgument(0))) {
                return matches(invocation.getArgument(0));
            }
            return lookups.incrementAndGet() == 1 ? List.of() : List.of(raced);
        });
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate email"));

        service.createAccessor(projectA.getId(), accessor("Ada@Example.com", "other-password"), projectAdmin.getId());

        assertEquals("original-hash", raced.getPassword());
        assertEquals("USER", raced.getSystemRole().getName());
        assertEquals(1, membershipsFor(raced.getId()).size());
        verify(passwordEncoder).encode("other-password");
        verify(notificationHelper).notifyUser(
                eq(raced.getId()),
                eq("Project Assignment"),
                contains("as Accessor."),
                eq(NotificationType.PROJECT)
        );
    }

    @Test
    void invitationAcceptanceReusesSameTenantAccountAndDoesNotResetPassword() {
        User existing = accessorOnProjectA();
        existing.setSystemRole(roles.get("TENANT_ADMIN"));
        ProjectInvitation invitation = pendingInvitation(" Token-1 ", "  Alice@Example.com ", "ACCESSOR");
        when(projectInvitationRepository.findByToken("Token-1")).thenReturn(Optional.of(invitation));
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setPassword("new-password-from-invite");
        request.setFirstName("Changed");

        service.acceptInvitation(" Token-1 ", request);

        assertEquals("kept-hash", existing.getPassword());
        assertEquals("Existing", existing.getFirstName());
        assertEquals("TENANT_ADMIN", existing.getSystemRole().getName());
        assertEquals("ACCEPTED", invitation.getStatus());
        assertEquals("alice@example.com", invitation.getEmail());
        assertTrue(membershipsFor(existing.getId()).stream()
                .anyMatch(membership -> membership.getProjectId().equals(projectB.getId())));
        assertEquals(0, userInserts.get());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void invitationAcceptanceCreatesOneVerifiedUserWhenEmailIsNew() {
        ProjectInvitation invitation = pendingInvitation("token-new", "new.invite@example.com", "QUALITY_CHECKER");
        when(projectInvitationRepository.findByToken("token-new")).thenReturn(Optional.of(invitation));
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setFirstName("Nia");
        request.setLastName("Cole");
        request.setPhone("9876543210");
        request.setPassword("invite-password");

        service.acceptInvitation("token-new", request);

        User created = usersByEmail.get("new.invite@example.com");
        assertEquals(1, userInserts.get());
        assertTrue(created.getEmailVerified());
        assertEquals("ENC:invite-password", created.getPassword());
        assertEquals("USER", created.getSystemRole().getName());
        assertEquals(roles.get("QUALITY_CHECKER").getId(), membershipsFor(created.getId()).get(0).getRoleId());
        assertEquals("ACCEPTED", invitation.getStatus());
    }

    @Test
    void invitationRejectsCrossTenantEmailAndInvalidTokens() {
        store(user("outsider@example.com", tenantB, roles.get("USER"), "outsider-hash"));
        ProjectInvitation crossTenant = pendingInvitation("token-cross", "outsider@example.com", "ACCESSOR");
        ProjectInvitation expired = pendingInvitation("token-expired", "brand.new@example.com", "ACCESSOR");
        expired.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        ProjectInvitation accepted = pendingInvitation("token-used", "brand.new@example.com", "ACCESSOR");
        accepted.setStatus("ACCEPTED");
        when(projectInvitationRepository.findByToken("token-cross")).thenReturn(Optional.of(crossTenant));
        when(projectInvitationRepository.findByToken("token-expired")).thenReturn(Optional.of(expired));
        when(projectInvitationRepository.findByToken("token-used")).thenReturn(Optional.of(accepted));
        when(projectInvitationRepository.findByToken("missing")).thenReturn(Optional.empty());

        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setFirstName("Nia");
        request.setPassword("invite-password");

        assertEquals(
                "User belongs to another tenant.",
                assertThrows(BadRequestException.class, () -> service.acceptInvitation("token-cross", request)).getMessage()
        );
        assertEquals(
                "Invitation has expired.",
                assertThrows(BadRequestException.class, () -> service.acceptInvitation("token-expired", request)).getMessage()
        );
        assertEquals(
                "Invitation is no longer pending.",
                assertThrows(BadRequestException.class, () -> service.acceptInvitation("token-used", request)).getMessage()
        );
        assertEquals(
                "Invitation is invalid.",
                assertThrows(BadRequestException.class, () -> service.acceptInvitation("missing", request)).getMessage()
        );
        assertEquals("PENDING", crossTenant.getStatus());
        assertEquals(0, userInserts.get());
    }

    @Test
    void inactiveMembershipIsReactivatedInsteadOfDuplicated() {
        User existing = accessorOnProjectA();
        ProjectUser inactive = membership(projectB, existing, "QUALITY_CHECKER");
        inactive.setActive(false);
        UUID membershipId = inactive.getId();

        service.createAccessor(projectB.getId(), accessor("alice@example.com", "ignored"), projectAdmin.getId());

        assertEquals(membershipId, inactive.getId());
        assertTrue(inactive.getActive());
        assertEquals(roles.get("ACCESSOR").getId(), inactive.getRoleId());
        assertEquals(1, memberships.stream()
                .filter(membership -> membership.getProjectId().equals(projectB.getId())
                        && membership.getUserId().equals(existing.getId()))
                .count());
        assertEquals(0, userInserts.get());
    }

    private User accessorOnProjectA() {
        User existing = store(user("alice@example.com", tenantA, roles.get("USER"), "kept-hash"));
        existing.setFirstName("Existing");
        existing.setLastName("Person");
        existing.setPhone("1111111111");
        membership(projectA, existing, "ACCESSOR");
        return existing;
    }

    private CreateAccessorRequest accessor(String email, String password) {
        return CreateAccessorRequest.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email(email)
                .phone("9876543210")
                .password(password)
                .build();
    }

    private CreateQualityCheckerRequest qualityChecker(String email) {
        return CreateQualityCheckerRequest.builder()
                .firstName("Quinn")
                .lastName("Checker")
                .email(email)
                .phone("9876543210")
                .password("qc-password")
                .build();
    }

    private CreateProjectAdminRequest projectAdminRequest(String email) {
        CreateProjectAdminRequest request = new CreateProjectAdminRequest();
        request.setFirstName("Pat");
        request.setLastName("Admin");
        request.setEmail(email);
        request.setPhone("9876543210");
        request.setPassword("admin-password");
        return request;
    }

    private ImportProjectMemberRequest importRow(String email, ProjectRole role) {
        ImportProjectMemberRequest row = new ImportProjectMemberRequest();
        row.setFirstName("Ada");
        row.setLastName("Lovelace");
        row.setEmail(email);
        row.setPhone("9876543210");
        row.setPassword("import-password");
        row.setRole(role);
        return row;
    }

    private ProjectInvitation pendingInvitation(String token, String email, String roleName) {
        ProjectInvitation invitation = new ProjectInvitation();
        invitation.setId(UUID.randomUUID());
        invitation.setProject(projectB);
        invitation.setEmail(email);
        invitation.setRole(roles.get(roleName));
        invitation.setToken(token);
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(3));
        invitation.setActive(true);
        return invitation;
    }

    private Project project(UUID tenantId, String name) {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setTenantId(tenantId);
        project.setName(name);
        project.setActive(true);
        return project;
    }

    private User user(String email, UUID tenantId, Role systemRole, String password) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(EmailAddress.normalize(email));
        user.setTenantId(tenantId);
        user.setSystemRole(systemRole);
        user.setPassword(password);
        user.setFirstName("Existing");
        user.setLastName("Person");
        user.setPhone("1111111111");
        user.setActive(true);
        user.setEmailVerified(false);
        return user;
    }

    private User store(User user) {
        user.setEmail(EmailAddress.normalize(user.getEmail()));
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    private List<User> matches(String email) {
        String normalized = EmailAddress.normalize(email);
        if (normalized == null) {
            return List.of();
        }
        return usersByEmail.values().stream()
                .filter(user -> normalized.equals(EmailAddress.normalize(user.getEmail())))
                .toList();
    }

    private ProjectUser membership(Project project, User user, String roleName) {
        ProjectUser membership = new ProjectUser();
        membership.setId(UUID.randomUUID());
        membership.setProjectId(project.getId());
        membership.setUserId(user.getId());
        membership.setRoleId(roles.get(roleName).getId());
        membership.setActive(true);
        memberships.add(membership);
        return membership;
    }

    private boolean activeMembership(UUID projectId, UUID userId, UUID roleId) {
        return memberships.stream().anyMatch(membership ->
                membership.getProjectId().equals(projectId)
                        && membership.getUserId().equals(userId)
                        && (roleId == null || roleId.equals(membership.getRoleId()))
                        && !Boolean.FALSE.equals(membership.getActive()));
    }

    private List<ProjectUser> membershipsFor(UUID userId) {
        return memberships.stream()
                .filter(membership -> userId.equals(membership.getUserId()))
                .toList();
    }
}
