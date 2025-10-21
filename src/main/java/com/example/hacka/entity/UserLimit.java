package com.example.hacka.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_limits")
@Data
public class UserLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "model_type", nullable = false)
    private String modelType;

    @Column(name = "request_limit_per_window")
    private Integer requestLimitPerWindow;

    @Column(name = "token_limit_per_window")
    private Integer tokenLimitPerWindow;

    @Column(name = "time_window_minutes", nullable = false)
    private Integer timeWindowMinutes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
