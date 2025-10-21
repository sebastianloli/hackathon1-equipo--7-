package com.example.hacka.dto;

import lombok.Data;

@Data
public class RestrictionDTO {
    private Long id;
    private Long companyId;
    private String modelType;
    private Integer usageLimit;
}
