package com.example.qcollect.role.repository;


import com.example.qcollect.role.entity.Role;
//import com.qcollect.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

}