package com.example.qcollect.common.jackson;

import com.example.qcollect.auth.dto.LoginRequest;
import com.example.qcollect.auth.dto.RegisterRequest;
import com.example.qcollect.project.dto.CreateAccessorRequest;
import com.example.qcollect.project.dto.CreateProjectAdminRequest;
import com.example.qcollect.project.dto.CreateQualityCheckerRequest;
import com.example.qcollect.project.dto.ImportProjectMemberRequest;
import com.example.qcollect.project.dto.request.InviteUserRequest;
import com.example.qcollect.user.EmailAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailTrimValidationTest {

    private static ObjectMapper mapper;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void paddedEmailsAreTrimmedButNotLowercasedAndStillValidate() throws Exception {
        assertAccepted(CreateAccessorRequest.class, """
                {"firstName":"Ada","lastName":"Lovelace","email":"  Person@Example.com  ","phone":"9876543210","password":"secret123"}
                """);
        assertAccepted(CreateQualityCheckerRequest.class, """
                {"firstName":"Ada","lastName":"Lovelace","email":"  Person@Example.com  ","phone":"9876543210","password":"secret123"}
                """);
        assertAccepted(CreateProjectAdminRequest.class, """
                {"firstName":"Ada","email":"  Person@Example.com  ","password":"secret123"}
                """);
        assertAccepted(ImportProjectMemberRequest.class, """
                {"firstName":"Ada","email":"  Person@Example.com  ","role":"ACCESSOR"}
                """);
        assertAccepted(InviteUserRequest.class, """
                {"email":"  Person@Example.com  "}
                """);
        assertAccepted(LoginRequest.class, """
                {"email":"  Person@Example.com  ","password":"secret123"}
                """);
        assertAccepted(RegisterRequest.class, """
                {"firstName":"Ada","email":"  Person@Example.com  ","password":"secret123"}
                """);
    }

    @Test
    void invalidEmailsStillFailValidationAfterTrim() throws Exception {
        assertEmailRejected(CreateAccessorRequest.class, """
                {"firstName":"Ada","lastName":"Lovelace","email":"  not-an-email  ","phone":"9876543210","password":"secret123"}
                """);
        assertEmailRejected(CreateQualityCheckerRequest.class, """
                {"firstName":"Ada","lastName":"Lovelace","email":"  not-an-email  ","phone":"9876543210","password":"secret123"}
                """);
        assertEmailRejected(CreateProjectAdminRequest.class, """
                {"firstName":"Ada","email":"  not-an-email  ","password":"secret123"}
                """);
        assertEmailRejected(ImportProjectMemberRequest.class, """
                {"firstName":"Ada","email":"  not-an-email  ","role":"ACCESSOR"}
                """);
        assertEmailRejected(InviteUserRequest.class, """
                {"email":"  not-an-email  "}
                """);
        assertEmailRejected(LoginRequest.class, """
                {"email":"  not-an-email  ","password":"secret123"}
                """);
        assertEmailRejected(RegisterRequest.class, """
                {"firstName":"Ada","email":"  not-an-email  ","password":"secret123"}
                """);
    }

    private static void assertAccepted(Class<?> type, String json) throws Exception {
        Object request = mapper.readValue(json, type);
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), violations.toString());
        String email = (String) type.getMethod("getEmail").invoke(request);
        assertEquals("Person@Example.com", email);
        assertEquals("person@example.com", EmailAddress.normalize(email));
    }

    private static void assertEmailRejected(Class<?> type, String json) throws Exception {
        Object request = mapper.readValue(json, type);
        String email = (String) type.getMethod("getEmail").invoke(request);
        assertEquals("not-an-email", email);
        boolean emailViolation = validator.validate(request).stream()
                .anyMatch(violation -> "email".equals(violation.getPropertyPath().toString()));
        assertTrue(emailViolation);
    }
}
