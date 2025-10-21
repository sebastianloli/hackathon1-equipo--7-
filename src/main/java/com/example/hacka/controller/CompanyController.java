package com.example.hacka.controller;

import com.example.hacka.dto.RestrictionDTO;
import com.example.hacka.dto.UserDTO;
import com.example.hacka.dto.UserLimitDTO;
import com.example.hacka.repository.RequestRepository;
import com.example.hacka.service.RestrictionService;
import com.example.hacka.service.UserLimitService;
import com.example.hacka.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
@PreAuthorize("hasAuthority('ROLE_COMPANY_ADMIN')")
public class CompanyController {

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserLimitService userLimitService;

    @Autowired
    private RequestRepository requestRepository;

    @PostMapping("/restrictions")
    public ResponseEntity<RestrictionDTO> createRestriction(@RequestBody RestrictionDTO dto) {
        return ResponseEntity.ok(restrictionService.createRestriction(dto));
    }

    @GetMapping("/restrictions")
    public ResponseEntity<List<RestrictionDTO>> getRestrictions(@RequestParam Long companyId) {
        return ResponseEntity.ok(restrictionService.getRestrictionsByCompany(companyId));
    }

    @PutMapping("/restrictions/{id}")
    public ResponseEntity<RestrictionDTO> updateRestriction(@PathVariable Long id, @RequestBody RestrictionDTO dto) {
        return ResponseEntity.ok(restrictionService.updateRestriction(id, dto));
    }

    @DeleteMapping("/restrictions/{id}")
    public ResponseEntity<Void> deleteRestriction(@PathVariable Long id) {
        restrictionService.deleteRestriction(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody Map<String, String> request) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(request.get("username"));
        userDTO.setEmail(request.get("email"));
        userDTO.setRole("ROLE_USER");
        userDTO.setCompanyId(Long.parseLong(request.get("companyId")));

        return ResponseEntity.ok(userService.createUser(userDTO, request.get("password")));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam Long companyId) {
        return ResponseEntity.ok(userService.getUsersByCompany(companyId));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PostMapping("/users/{id}/limits")
    public ResponseEntity<UserLimitDTO> assignLimit(@PathVariable Long id, @RequestBody UserLimitDTO dto) {
        dto.setUserId(id);
        return ResponseEntity.ok(userLimitService.assignLimit(dto));
    }

    @GetMapping("/users/{id}/consumption")
    public ResponseEntity<Map<String, Object>> getUserConsumption(@PathVariable Long id) {
        var requests = requestRepository.findByUserId(id);

        int totalTokens = requests.stream()
                .mapToInt(r -> r.getTokensConsumed() != null ? r.getTokensConsumed() : 0)
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("totalRequests", requests.size());
        result.put("totalTokens", totalTokens);

        return ResponseEntity.ok(result);
    }
}
