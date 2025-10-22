package com.example.hacka.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SaleDTO {
    private Long id;
    private String sku;
    private Integer units;
    private Double price;
    private String branch;
    private LocalDateTime soldAt;
    private String createdBy;
}
