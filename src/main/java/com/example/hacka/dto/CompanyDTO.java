package com.example.hacka.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyDTO {
    private Long id;
    private String name;
    private String ruc;
    private LocalDateTime affiliationDate;
    private Boolean active;
}
