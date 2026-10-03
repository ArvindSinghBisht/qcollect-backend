package com.example.qcollect.tenant.repository;


import com.example.qcollect.tenant.entity.Tenant;
//import com.qcollect.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    boolean existsByCode(String code);

    Optional<Tenant> findByCode(String code);

}