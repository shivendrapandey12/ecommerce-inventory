package com.shivendra.ecommerce_backend.service;

import com.shivendra.ecommerce_backend.dto.LoginRequestDTO;
import com.shivendra.ecommerce_backend.dto.LoginResponseDTO;
import com.shivendra.ecommerce_backend.dto.RegisterRequestDTO;
import com.shivendra.ecommerce_backend.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO registerUser(RegisterRequestDTO request);

    LoginResponseDTO login(LoginRequestDTO request);
}