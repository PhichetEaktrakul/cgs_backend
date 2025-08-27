package com.base.encode.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.base.encode.model.AuthRequest;
import com.base.encode.model.AuthResponse;
import com.base.encode.model.RegisterRequest;
import com.base.encode.model.Users;
import com.base.encode.repository.UserRepository;
import com.base.encode.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token);
    }

    // --- REGISTER ---
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole()); // "USER" or "ADMIN"
        userRepository.save(user);

        // Generate JWT after registration
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
    /*
     * @PostMapping("/logout")
     * public ResponseEntity<?> logout(HttpServletRequest request) {
     * String token = jwtUtil.extractJwtFromRequest(request);
     * if (token == null || token.isBlank()) {
     * return ResponseEntity.badRequest().body("Invalid token");
     * }
     * LocalDateTime expiry = jwtUtil.extractExpiryFromToken(token);
     * tokenBlacklistService.blacklistToken(token, expiry);
     * 
     * return ResponseEntity.ok("Logged out");
     */
}
