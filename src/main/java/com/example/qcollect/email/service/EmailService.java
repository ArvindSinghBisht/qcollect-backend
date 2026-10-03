package com.example.qcollect.email.service;

public interface EmailService {

    void sendProjectInvitation(
            String email,
            String projectName,
            String token
    );

}