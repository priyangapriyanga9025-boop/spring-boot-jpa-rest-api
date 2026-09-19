package com.rabtech.api.service;

import com.rabtech.api.dto.AuthResponse;
import com.rabtech.api.dto.LoginRequest;
import com.rabtech.api.dto.RegisterRequest;
import com.rabtech.api.entity.User;
import com.rabtech.api.repository.UserRepository;
import com.rabtech.api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already registered");
        }

        String role = request.getRole() == null || request.getRole().isBlank()
                ? "USER"
                : request.getRole().toUpperCase();
        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), role);
        User savedUser = userRepository.save(user);
        return new AuthResponse(null, savedUser.getUsername(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new AuthResponse(jwtService.generateToken(userDetails), user.getUsername(), user.getRole());
    }
}