package com.example.hacka.dto;

import lombok.Data;

@Data
public class SalesAggregates {
    private Integer totalUnits;
    private Double totalRevenue;
    private String topSku;
    private String topBranch;
}
