package com.jayaa.blog.service;

import com.jayaa.blog.dto.*;
import com.jayaa.blog.model.User;
import com.jayaa.blog.repository.UserRepository;
import com.jayaa.blog.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setBio(request.getBio());
        user.setRole("AUTHOR"); // Default role
        user.setEnabled(true);

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(AuthRequest request) {
        // Find user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Check if enabled
        if (!user.getEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }
}