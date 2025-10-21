package com.example.hacka.controller;

import com.example.hacka.dto.CompanyDTO;
import com.example.hacka.repository.RequestRepository;
import com.example.hacka.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_SPARKY_ADMIN')")
public class AdminController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private RequestRepository requestRepository;

    @PostMapping("/companies")
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody Map<String, Object> request) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setName((String) request.get("name"));
        companyDTO.setRuc((String) request.get("ruc"));

        String adminUsername = (String) request.get("adminUsername");
        String adminPassword = (String) request.get("adminPassword");

        CompanyDTO created = companyService.createCompany(companyDTO, adminUsername, adminPassword);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @RequestBody CompanyDTO dto) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto));
    }

    @PatchMapping("/companies/{id}/status")
    public ResponseEntity<Void> toggleCompanyStatus(@PathVariable Long id) {
        companyService.toggleCompanyStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/companies/{id}/consumption")
    public ResponseEntity<Map<String, Object>> getCompanyConsumption(@PathVariable Long id) {
        var requests = requestRepository.findByCompanyId(id);

        int totalTokens = requests.stream()
                .mapToInt(r -> r.getTokensConsumed() != null ? r.getTokensConsumed() : 0)
                .sum();

        Map<String, Long> byModel = new HashMap<>();
        requests.forEach(r -> {
            byModel.put(r.getModelUsed(), byModel.getOrDefault(r.getModelUsed(), 0L) + 1);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("totalRequests", requests.size());
        result.put("totalTokens", totalTokens);
        result.put("requestsByModel", byModel);

        return ResponseEntity.ok(result);
    }
}
