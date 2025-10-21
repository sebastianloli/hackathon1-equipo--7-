package com.example.hacka.dto;

import lombok.Data;

@Data
public class UserLimitDTO {
    private Long id;
    private Long userId;
    private String modelType;
    private Integer requestLimitPerWindow;
    private Integer tokenLimitPerWindow;
    private Integer timeWindowMinutes;
}