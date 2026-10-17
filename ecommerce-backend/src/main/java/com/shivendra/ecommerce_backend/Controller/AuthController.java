package com.shivendra.ecommerce_backend.controller;

import com.shivendra.ecommerce_backend.dto.LoginRequestDTO;
import com.shivendra.ecommerce_backend.dto.LoginResponseDTO;
import com.shivendra.ecommerce_backend.dto.RegisterRequestDTO;
import com.shivendra.ecommerce_backend.dto.UserResponseDTO;
import com.shivendra.ecommerce_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody RegisterRequestDTO request) {

        UserResponseDTO response =
                userService.registerUser(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response =
                userService.login(request);

        return ResponseEntity.ok(response);
    }
}