package com.example.hacka.controller;

import com.example.hacka.entity.User;
import com.example.hacka.repository.UserRepository;
import com.example.hacka.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        System.out.println("=== LOGIN REQUEST RECEIVED ===");
        String username = credentials.get("username");
        String password = credentials.get("password");

        System.out.println("Username: " + username);
        System.out.println("Password provided: " + (password != null ? "yes" : "no"));

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            System.out.println("User not found: " + username);
            return ResponseEntity.status(401).body("Invalid credentials - user not found");
        }

        System.out.println("User found: " + user.getUsername());
        System.out.println("Password matches: " + passwordEncoder.matches(password, user.getPassword()));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Password does not match");
            return ResponseEntity.status(401).body("Invalid credentials - wrong password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());
        response.put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);

        System.out.println("Login successful for: " + username);
        System.out.println("=== END LOGIN REQUEST ===");

        return ResponseEntity.ok(response);
    }
}