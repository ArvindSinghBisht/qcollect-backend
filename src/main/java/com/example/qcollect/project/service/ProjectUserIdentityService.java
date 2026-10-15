package com.example.qcollect.project.service;

import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.project.entity.Project;
import com.example.qcollect.role.entity.Role;
import com.example.qcollect.role.repository.RoleRepository;
import com.example.qcollect.user.EmailAddress;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectUserIdentityService {

    static final String SYSTEM_USER_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectUserAccountWriter accountWriter;
    private final PasswordEncoder passwordEncoder;

    public User getOrCreateProjectUser(
            Project project,
            String email,
            String firstName,
            String lastName,
            String phone,
            String password
    ) {
        return resolveProjectUser(
                project,
                email,
                firstName,
                lastName,
                phone,
                password
        ).user();
    }

    public ProjectUserAccount resolveProjectUser(
            Project project,
            String email,
            String firstName,
            String lastName,
            String phone,
            String password
    ) {
        String normalizedEmail = requireEmail(email);
        User existing = findExisting(normalizedEmail);
        if (existing != null) {
            return reuse(project, existing);
        }

        User draft = newUser(project, normalizedEmail, firstName, lastName, phone, password);
        try {
            return new ProjectUserAccount(accountWriter.insertNew(draft), true);
        } catch (DataIntegrityViolationException ex) {
            User raced = findExisting(normalizedEmail);
            if (raced == null) {
                throw ex;
            }
            return reuse(project, raced);
        }
    }

    private ProjectUserAccount reuse(Project project, User user) {
        if (user.getTenantId() == null
                || project.getTenantId() == null
                || !project.getTenantId().equals(user.getTenantId())) {
            throw new BadRequestException("User belongs to another tenant.");
        }
        return new ProjectUserAccount(user, false);
    }

    private User newUser(
            Project project,
            String email,
            String firstName,
            String lastName,
            String phone,
            String password
    ) {
        if (!StringUtils.hasText(firstName)) {
            throw new BadRequestException("First name is required for a new user.");
        }
        if (!StringUtils.hasText(password)) {
            throw new BadRequestException("Password is required for a new user.");
        }

        Role systemUserRole = roleRepository.findByName(SYSTEM_USER_ROLE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("USER role not found."));

        User user = new User();
        user.setTenantId(project.getTenantId());
        user.setSystemRole(systemUserRole);
        user.setFirstName(firstName.trim());
        user.setLastName(lastName == null ? null : lastName.trim());
        user.setEmail(email);
        user.setPhone(phone == null ? null : phone.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setEmailVerified(false);
        return user;
    }

    private User findExisting(String normalizedEmail) {
        List<User> matches = userRepository.findAllByNormalizedEmail(normalizedEmail);
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            throw new BadRequestException(
                    "Multiple accounts match this email. Resolve the duplicate emails before assigning this person."
            );
        }
        return matches.get(0);
    }

    private String requireEmail(String email) {
        String normalized = EmailAddress.normalize(email);
        if (normalized == null) {
            throw new BadRequestException("Email is required.");
        }
        return normalized;
    }
}
