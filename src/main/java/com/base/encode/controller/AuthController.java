package com.base.encode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.base.encode.model.DTO.LoginRequest;
import com.base.encode.model.DTO.RegisterRequest;
import com.base.encode.service.UserService;
import com.base.encode.model.DTO.AuthResponse;
import com.base.encode.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var userOpt = userService.findUserByUsername(request.getUsername());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().passwordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        String token = jwtUtil.generateToken(userOpt.get().username());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.findUserByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        userService.createUser(request.getUsername(), passwordHash, request.getRole());

        return ResponseEntity.ok(Map.of("message", "Register success"));
    }

}