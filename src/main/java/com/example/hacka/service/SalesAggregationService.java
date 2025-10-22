package com.example.hacka.service;

import com.example.hacka.dto.SalesAggregates;
import com.example.hacka.entity.Sale;
import com.example.hacka.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesAggregationService {

    @Autowired
    private SaleRepository saleRepository;

    public SalesAggregates calculateAggregates(LocalDateTime from, LocalDateTime to, String branch) {
        List<Sale> sales;

        if (branch != null) {
            sales = saleRepository.findByBranchAndSoldAtBetween(branch, from, to);
        } else {
            sales = saleRepository.findBySoldAtBetween(from, to);
        }

        SalesAggregates aggregates = new SalesAggregates();

        // Total units
        aggregates.setTotalUnits(sales.stream()
                .mapToInt(Sale::getUnits)
                .sum());

        // Total revenue
        aggregates.setTotalRevenue(sales.stream()
                .mapToDouble(s -> s.getUnits() * s.getPrice())
                .sum());

        // Top SKU by units
        Map<String, Integer> skuUnits = sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getSku,
                        Collectors.summingInt(Sale::getUnits)
                ));

        aggregates.setTopSku(skuUnits.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null));

        // Top branch by sales count
        Map<String, Long> branchCounts = sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getBranch,
                        Collectors.counting()
                ));

        aggregates.setTopBranch(branchCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null));

        return aggregates;
    }
}
