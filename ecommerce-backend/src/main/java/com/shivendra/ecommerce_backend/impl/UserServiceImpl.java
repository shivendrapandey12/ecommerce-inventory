package com.shivendra.ecommerce_backend.service.impl;

import com.shivendra.ecommerce_backend.dto.LoginRequestDTO;
import com.shivendra.ecommerce_backend.dto.LoginResponseDTO;
import com.shivendra.ecommerce_backend.dto.RegisterRequestDTO;
import com.shivendra.ecommerce_backend.dto.UserResponseDTO;
import com.shivendra.ecommerce_backend.entity.User;
import com.shivendra.ecommerce_backend.repository.UserRepository;
import com.shivendra.ecommerce_backend.security.JwtService;
import com.shivendra.ecommerce_backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Constructor Injection
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================
    // REGISTER USER
    // =========================

    @Override
    public UserResponseDTO registerUser(RegisterRequestDTO request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Create User entity
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // Encode password before storing it
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setPhoneNumber(request.getPhoneNumber());

        // Every normal registration creates a CUSTOMER
        user.setRole("CUSTOMER");

        // Save user in database
        User savedUser = userRepository.save(user);

        // Create response DTO
        UserResponseDTO response = new UserResponseDTO();

        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setPhoneNumber(savedUser.getPhoneNumber());
        response.setRole(savedUser.getRole());

        return response;
    }

    // =========================
    // LOGIN USER
    // =========================

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1. Find user using email
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        // 2. Compare entered password
        // with encrypted password stored in database
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        // 3. If password is wrong
        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        // 4. Generate JWT token
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        // 5. Return token + user information
        return new LoginResponseDTO(
                token,
                user.getEmail(),
                user.getRole()
        );
    }
}