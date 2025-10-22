package com.example.hacka.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data

public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Integer units;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String branch;

    @Column(name = "sold_at", nullable = false)
    private LocalDateTime soldAt;

    @Column(name = "created_by")
    private String createdBy; // username del usuario que creó la venta

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

