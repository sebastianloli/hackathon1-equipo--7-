package com.example.hacka.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String queryText;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "tokens_consumed")
    private Integer tokensConsumed;

    @Column(name = "model_used", nullable = false)
    private String modelUsed;

    @Column(name = "success", nullable = false)
    private Boolean success = true;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
