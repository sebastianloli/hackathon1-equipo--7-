package com.example.hacka.controller;

import com.example.hacka.entity.ReportRequest;
import com.example.hacka.entity.User;
import com.example.hacka.event.ReportRequestedEvent;
import com.example.hacka.repository.ReportRequestRepository;
import com.example.hacka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/sales/summary")
public class SummaryController {

    @Autowired
    private ReportRequestRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/weekly")
    public ResponseEntity<?> requestWeeklySummary(@RequestBody Map<String, String> request, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "UNAUTHORIZED"));
        }

        String fromStr = request.get("from");
        String toStr = request.get("to");
        String branch = request.get("branch");
        String emailTo = request.get("emailTo");

        // Validaciones
        if (emailTo == null || emailTo.isBlank()) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "BAD_REQUEST",
                    "message", "emailTo es obligatorio"
            ));
        }

        // Si es BRANCH, forzar su sucursal
        if (user.getRole() == User.Role.BRANCH) {
            branch = user.getBranch();
        }

        // Si no se especifican fechas, calcular última semana
        LocalDateTime from = fromStr != null ? LocalDateTime.parse(fromStr) : LocalDateTime.now().minusWeeks(1);
        LocalDateTime to = toStr != null ? LocalDateTime.parse(toStr) : LocalDateTime.now();

        ReportRequest report = new ReportRequest();
        report.setFromDate(from);
        report.setToDate(to);
        report.setBranch(branch);
        report.setEmailTo(emailTo);
        report.setStatus(ReportRequest.Status.PROCESSING);
        report.setMessage("Su solicitud de reporte está siendo procesada. Recibirá el resumen en " + emailTo + " en 30-60 segundos");
        report.setEstimatedTime("30-60 segundos");
        report.setRequestedBy(user.getUsername());

        report = reportRepository.save(report);

        // Publicar evento para procesamiento asíncrono
        eventPublisher.publishEvent(new ReportRequestedEvent(this, report.getRequestId()));

        return ResponseEntity.status(202).body(Map.of(
                "requestId", report.getRequestId(),
                "status", report.getStatus().name(),
                "message", report.getMessage(),
                "estimatedTime", report.getEstimatedTime(),
                "requestedAt", report.getRequestedAt()
        ));
    }
}
