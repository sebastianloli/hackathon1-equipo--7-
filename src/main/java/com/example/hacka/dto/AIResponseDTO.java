package com.example.hacka.dto;

import lombok.Data;

@Data
public class AIResponseDTO {
    private String response;
    private Integer tokensUsed;
    private String model;
}
