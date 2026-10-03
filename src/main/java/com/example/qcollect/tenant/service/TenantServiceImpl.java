package com.example.qcollect.tenant.service;

import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.role.entity.Role;
import com.example.qcollect.role.repository.RoleRepository;
import com.example.qcollect.tenant.dto.CreateTenantAdminRequest;
import com.example.qcollect.tenant.dto.CreateTenantRequest;
import com.example.qcollect.tenant.dto.TenantResponse;
import com.example.qcollect.tenant.entity.Tenant;
import com.example.qcollect.tenant.repository.TenantRepository;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantServiceImpl implements TenantService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;

    @Override
    public TenantResponse createTenant(CreateTenantRequest request) {

        if (tenantRepository.existsByCode(request.getTenantCode())) {
            throw new BadRequestException("Tenant code already exists.");
        }

        Tenant tenant = new Tenant();

        tenant.setName(request.getTenantName());

        tenant.setCode(request.getTenantCode());

        tenant.setLogoUrl(request.getLogoUrl());

        tenant.setActive(true);

        tenant = tenantRepository.save(tenant);

        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .code(tenant.getCode())
                .active(tenant.getActive())
                .build();
    }
    @Override
    public void createTenantAdmin(

            UUID tenantId,

            CreateTenantAdminRequest request
    ) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new BadRequestException("Tenant not found."));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }

        Role tenantAdminRole =
                roleRepository.findByName("TENANT_ADMIN")
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "TENANT_ADMIN role not found."
                                ));

        User user = new User();

        user.setTenantId(tenant.getId());

        user.setSystemRole(tenantAdminRole);

        user.setFirstName(request.getFirstName());

        user.setLastName(request.getLastName());

        user.setEmail(request.getEmail());

        user.setPhone(request.getPhone());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setActive(true);

        userRepository.save(user);
    }
}