package com.example.qcollect.auth.controller;

//package com.qcollect.auth.controller;

import com.example.qcollect.auth.dto.LoginRequest;
import com.example.qcollect.auth.dto.LoginResponse;
import com.example.qcollect.auth.dto.RegisterRequest;
import com.example.qcollect.auth.service.AuthService;
import com.example.qcollect.common.dto.ApiResponse;
//import com.qcollect.auth.dto.LoginRequest;
//import com.qcollect.auth.dto.LoginResponse;
//import com.qcollect.auth.dto.RegisterRequest;
//import com.qcollect.auth.service.AuthService;
//import com.qcollect.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<?> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ApiResponse.builder()
                .success(true)
                .message("User Registered Successfully")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login Successful")
                .data(authService.login(request))
                .build();

    }

}