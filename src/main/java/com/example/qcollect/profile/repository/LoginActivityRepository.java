package com.example.qcollect.profile.repository;

import com.example.qcollect.profile.entity.LoginActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoginActivityRepository
        extends JpaRepository<LoginActivity, UUID> {

    List<LoginActivity> findByUserIdOrderByLoginTimeDesc(
            UUID userId
    );

    Optional<LoginActivity> findByUserIdAndActiveSessionTrue(
            UUID userId
    );

}