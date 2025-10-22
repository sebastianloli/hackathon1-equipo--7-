package com.example.hacka.service;

import com.example.hacka.dto.SalesAggregates;
import com.example.hacka.entity.Sale;
import com.example.hacka.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @Mock
    private SaleRepository salesRepository;

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<Sale> mockSales = Arrays.asList(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 5, 2.49, "San Isidro"),
                createSale("OREO_CLASSIC", 15, 1.99, "Miraflores")
        );

        when(salesRepository.findBySoldAtBetween(any(), any())).thenReturn(mockSales);

        // When
        SalesAggregates result = salesAggregationService.calculateAggregates(
                now.minusDays(7), now, null
        );

        // Then
        assertNotNull(result);
        assertEquals(30, result.getTotalUnits());
        assertEquals(42.43, result.getTotalRevenue(), 0.01);
        assertEquals("OREO_CLASSIC", result.getTopSku());
        assertEquals("Miraflores", result.getTopBranch());
    }

    @Test
    void shouldHandleEmptyListWhenNoSales() {
        // Given
        when(salesRepository.findBySoldAtBetween(any(), any())).thenReturn(Arrays.asList());

        // When
        SalesAggregates result = salesAggregationService.calculateAggregates(
                LocalDateTime.now().minusDays(7), LocalDateTime.now(), null
        );

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalUnits());
        assertEquals(0.0, result.getTotalRevenue());
        assertNull(result.getTopSku());
        assertNull(result.getTopBranch());
    }

    @Test
    void shouldFilterByBranchCorrectly() {
        // Given
        List<Sale> mockSales = Arrays.asList(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores")
        );

        when(salesRepository.findByBranchAndSoldAtBetween(eq("Miraflores"), any(), any()))
                .thenReturn(mockSales);

        // When
        SalesAggregates result = salesAggregationService.calculateAggregates(
                LocalDateTime.now().minusDays(7), LocalDateTime.now(), "Miraflores"
        );

        // Then
        verify(salesRepository, times(1))
                .findByBranchAndSoldAtBetween(eq("Miraflores"), any(), any());
    }

    @Test
    void shouldCalculateCorrectTopSkuWhenTied() {
        // Given
        List<Sale> mockSales = Arrays.asList(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 10, 2.49, "San Isidro")
        );

        when(salesRepository.findBySoldAtBetween(any(), any())).thenReturn(mockSales);

        // When
        SalesAggregates result = salesAggregationService.calculateAggregates(
                LocalDateTime.now().minusDays(7), LocalDateTime.now(), null
        );

        // Then
        assertNotNull(result.getTopSku());
        assertTrue(result.getTopSku().equals("OREO_CLASSIC") || result.getTopSku().equals("OREO_DOUBLE"));
    }

    @Test
    void shouldCalculateCorrectDateRange() {
        // Given
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();

        List<Sale> mockSales = Arrays.asList(createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"));
        when(salesRepository.findBySoldAtBetween(from, to)).thenReturn(mockSales);

        // When
        salesAggregationService.calculateAggregates(from, to, null);

        // Then
        verify(salesRepository, times(1)).findBySoldAtBetween(from, to);
    }

    private Sale createSale(String sku, int units, double price, String branch) {
        Sale sale = new Sale();
        sale.setSku(sku);
        sale.setUnits(units);
        sale.setPrice(price);
        sale.setBranch(branch);
        sale.setSoldAt(LocalDateTime.now());
        return sale;
    }
}
