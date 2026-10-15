package com.example.qcollect.auth.controller;

import com.example.qcollect.auth.dto.LoginRequest;
import com.example.qcollect.auth.dto.LoginResponse;
import com.example.qcollect.auth.security.JwtAuthenticationFilter;
import com.example.qcollect.auth.service.AuthService;
import com.example.qcollect.common.context.ProjectContextFilter;
import com.example.qcollect.common.web.EmailTrimRequestBodyAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, ProjectContextFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(EmailTrimRequestBodyAdvice.class)
class LoginEmailTrimMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void paddedLoginEmailReachesControllerTrimmed() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenAnswer(invocation -> {
            LoginRequest request = invocation.getArgument(0);
            return LoginResponse.builder()
                    .email(request.getEmail())
                    .build();
        });

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"  person@example.com  \",\"password\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("person@example.com"));

        verify(authService).login(org.mockito.ArgumentMatchers.argThat(
                request -> "person@example.com".equals(request.getEmail())
        ));
    }

    @Test
    void paddedNonEmailStillReturns400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"  not-an-email  \",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }
}
