package com.example.qcollect.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("https://surprising-insight-production-2c9d.up.railway.app")
    private String frontendUrl;

    @Override
    public void sendProjectInvitation(
            String email,
            String projectName,
            String token
    ) {

        String invitationLink =
                frontendUrl + "/invite/" + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "Invitation to join QCollect"
        );

        message.setText(
                """
                Hello,

                You have been invited to join the project:

                %s

                Click the link below to accept the invitation:

                %s

                This invitation expires in 7 days.

                Regards,
                QCollect Team
                """.formatted(projectName, invitationLink)
        );

        mailSender.send(message);
    }
}