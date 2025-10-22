package com.example.hacka.controller;

import com.example.hacka.entity.Sale;
import com.example.hacka.entity.User;
import com.example.hacka.repository.SaleRepository;
import com.example.hacka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sales")
public class SalesController {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody Map<String, Object> request, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "UNAUTHORIZED"));
        }

        // Validar que BRANCH solo pueda crear ventas de su sucursal
        String requestBranch = (String) request.get("branch");
        if (user.getRole() == User.Role.BRANCH) {
            if (!user.getBranch().equals(requestBranch)) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "FORBIDDEN",
                        "message", "Solo puedes crear ventas para tu sucursal asignada"
                ));
            }
        }

        Sale sale = new Sale();
        sale.setSku((String) request.get("sku"));
        sale.setUnits((Integer) request.get("units"));
        sale.setPrice((Double) request.get("price"));
        sale.setBranch(requestBranch);
        sale.setSoldAt(LocalDateTime.parse((String) request.get("soldAt")));
        sale.setCreatedBy(user.getUsername());

        sale = saleRepository.save(sale);

        return ResponseEntity.status(201).body(sale);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSale(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        Sale sale = saleRepository.findById(id).orElse(null);

        if (sale == null) {
            return ResponseEntity.status(404).body(Map.of("error", "NOT_FOUND"));
        }

        // BRANCH solo puede ver ventas de su sucursal
        if (user.getRole() == User.Role.BRANCH && !sale.getBranch().equals(user.getBranch())) {
            return ResponseEntity.status(403).body(Map.of("error", "FORBIDDEN"));
        }

        return ResponseEntity.ok(sale);
    }

    @GetMapping
    public ResponseEntity<?> getSales(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            Authentication auth
    ) {
        User user = userRepository.findByUsername(auth.getName()).orElse(null);

        // Si es BRANCH, forzar filtro por su sucursal
        if (user.getRole() == User.Role.BRANCH) {
            branch = user.getBranch();
        }

        List<Sale> sales;

        if (from != null && to != null && branch != null) {
            sales = saleRepository.findByBranchAndSoldAtBetween(
                    branch,
                    LocalDateTime.parse(from),
                    LocalDateTime.parse(to)
            );
        } else if (from != null && to != null) {
            sales = saleRepository.findBySoldAtBetween(
                    LocalDateTime.parse(from),
                    LocalDateTime.parse(to)
            );
        } else if (branch != null) {
            sales = saleRepository.findByBranch(branch);
        } else {
            sales = saleRepository.findAll();
        }

        return ResponseEntity.ok(sales);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @RequestBody Map<String, Object> request, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        Sale sale = saleRepository.findById(id).orElse(null);

        if (sale == null) {
            return ResponseEntity.status(404).body(Map.of("error", "NOT_FOUND"));
        }

        // BRANCH solo puede actualizar ventas de su sucursal
        if (user.getRole() == User.Role.BRANCH && !sale.getBranch().equals(user.getBranch())) {
            return ResponseEntity.status(403).body(Map.of("error", "FORBIDDEN"));
        }

        if (request.containsKey("sku")) sale.setSku((String) request.get("sku"));
        if (request.containsKey("units")) sale.setUnits((Integer) request.get("units"));
        if (request.containsKey("price")) sale.setPrice((Double) request.get("price"));
        if (request.containsKey("soldAt")) sale.setSoldAt(LocalDateTime.parse((String) request.get("soldAt")));

        sale = saleRepository.save(sale);
        return ResponseEntity.ok(sale);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElse(null);

        // Solo CENTRAL puede eliminar
        if (user.getRole() != User.Role.CENTRAL) {
            return ResponseEntity.status(403).body(Map.of("error", "FORBIDDEN"));
        }

        Sale sale = saleRepository.findById(id).orElse(null);
        if (sale == null) {
            return ResponseEntity.status(404).body(Map.of("error", "NOT_FOUND"));
        }

        saleRepository.deleteById(id);
        return ResponseEntity.status(204).build();
    }
}
