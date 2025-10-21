package com.example.hacka.controller;

import com.example.hacka.dto.AIRequestDTO;
import com.example.hacka.dto.AIResponseDTO;
import com.example.hacka.entity.Request;
import com.example.hacka.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody AIRequestDTO request, Authentication auth) {
        try {
            AIResponseDTO response = aiService.processRequest(auth.getName(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/completion")
    public ResponseEntity<?> completion(@RequestBody AIRequestDTO request, Authentication auth) {
        try {
            AIResponseDTO response = aiService.processRequest(auth.getName(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/multimodal")
    public ResponseEntity<?> multimodal(@RequestBody AIRequestDTO request, Authentication auth) {
        try {
            AIResponseDTO response = aiService.processRequest(auth.getName(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/models")
    public ResponseEntity<List<String>> getModels() {
        return ResponseEntity.ok(List.of("gpt-4", "meta-llama", "deepseek", "openai"));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Request>> getHistory(Authentication auth) {
        return ResponseEntity.ok(aiService.getUserHistory(auth.getName()));
    }
}
