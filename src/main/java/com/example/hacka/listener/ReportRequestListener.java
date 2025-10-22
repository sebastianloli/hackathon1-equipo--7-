package com.example.hacka.listener;

import com.example.hacka.dto.SalesAggregates;
import com.example.hacka.entity.ReportRequest;
import com.example.hacka.event.ReportRequestedEvent;
import com.example.hacka.repository.ReportRequestRepository;
import com.example.hacka.service.GitHubModelsService;
import com.example.hacka.service.SalesAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Component
public class ReportRequestListener {

    @Autowired
    private ReportRequestRepository reportRepository;

    @Autowired
    private SalesAggregationService aggregationService;

    @Autowired
    private GitHubModelsService gitHubModelsService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @EventListener
    public void handleReportRequest(ReportRequestedEvent event) {
        ReportRequest report = reportRepository.findByRequestId(event.getRequestId()).orElse(null);
        if (report == null) return;

        try {
            // 1. Calcular agregados
            SalesAggregates aggregates = aggregationService.calculateAggregates(
                    report.getFromDate(),
                    report.getToDate(),
                    report.getBranch()
            );

            // 2. Llamar a GitHub Models para generar resumen
            String summary = gitHubModelsService.generateSummary(aggregates);

            // 3. Enviar email
            sendEmail(report.getEmailTo(), summary, report.getFromDate(), report.getToDate());

            // 4. Actualizar estado
            report.setStatus(ReportRequest.Status.COMPLETED);
            report.setCompletedAt(LocalDateTime.now());
            report.setMessage("Resumen enviado exitosamente a " + report.getEmailTo());

        } catch (Exception e) {
            report.setStatus(ReportRequest.Status.FAILED);
            report.setMessage("Error: " + e.getMessage());
        }

        reportRepository.save(report);
    }

    private void sendEmail(String toEmail, String summary, LocalDateTime fromDate, LocalDateTime toDate) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);  // <-- Usa toEmail
        helper.setSubject("Reporte Semanal Oreo - [" + fromDate + "] a [" + toDate + "]");  // <-- Usa fromDate y toDate

        String htmlContent = "<html><body>" +
                "<h1>📊 Reporte Semanal Oreo</h1>" +
                "<div style='background: #f0f0f0; padding: 15px;'>" +
                summary +
                "</div>" +
                "</body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
