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
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");
        String branch = request.get("branch");

        // Validaciones
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "CONFLICT", "message", "Username ya existe"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "CONFLICT", "message", "Email ya existe"));
        }

        // Validar role
        if (!role.equals("CENTRAL") && !role.equals("BRANCH")) {
            return ResponseEntity.status(400).body(Map.of("error", "BAD_REQUEST", "message", "Role debe ser CENTRAL o BRANCH"));
        }

        // Validar branch
        if (role.equals("BRANCH") && (branch == null || branch.isBlank())) {
            return ResponseEntity.status(400).body(Map.of("error", "BAD_REQUEST", "message", "Branch obligatorio para ROLE_BRANCH"));
        }

        if (role.equals("CENTRAL") && branch != null) {
            branch = null; // CENTRAL no tiene branch
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf(role));
        user.setBranch(branch);

        user = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("branch", user.getBranch());
        response.put("createdAt", user.getCreatedAt());

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "UNAUTHORIZED", "message", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("expiresIn", 3600);
        response.put("role", user.getRole().name());
        response.put("branch", user.getBranch());

        return ResponseEntity.ok(response);
    }
}