package com.example.qcollect.auth.service;


import com.example.qcollect.auth.dto.LoginRequest;
import com.example.qcollect.auth.dto.LoginResponse;
import com.example.qcollect.auth.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

}