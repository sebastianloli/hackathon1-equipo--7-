package com.example.hacka.service;

import com.example.hacka.dto.RestrictionDTO;
import com.example.hacka.entity.Company;
import com.example.hacka.entity.Restriction;
import com.example.hacka.repository.CompanyRepository;
import com.example.hacka.repository.RestrictionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestrictionServiceTest {

    @Mock
    private RestrictionRepository restrictionRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private RestrictionService restrictionService;

    @Test
    void testCreateRestriction() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Test Corp");

        RestrictionDTO dto = new RestrictionDTO();
        dto.setCompanyId(1L);
        dto.setModelType("gpt-5-mini");
        dto.setUsageLimit(1000);

        Restriction restriction = new Restriction();
        restriction.setId(1L);
        restriction.setCompany(company);
        restriction.setModelType("gpt-5-mini");
        restriction.setUsageLimit(1000);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(restrictionRepository.save(any(Restriction.class))).thenReturn(restriction);

        RestrictionDTO result = restrictionService.createRestriction(dto);

        assertNotNull(result);
        assertEquals("gpt-5-mini", result.getModelType());
        assertEquals(1000, result.getUsageLimit());
        verify(restrictionRepository, times(1)).save(any(Restriction.class));
    }

    @Test
    void testGetRestrictionsByCompany() {
        Company company = new Company();
        company.setId(1L);

        Restriction restriction1 = new Restriction();
        restriction1.setId(1L);
        restriction1.setModelType("gpt-5-mini");
        restriction1.setCompany(company);

        Restriction restriction2 = new Restriction();
        restriction2.setId(2L);
        restriction2.setModelType("meta-llama");
        restriction2.setCompany(company);

        when(restrictionRepository.findByCompanyId(1L))
                .thenReturn(Arrays.asList(restriction1, restriction2));

        List<RestrictionDTO> results = restrictionService.getRestrictionsByCompany(1L);

        assertEquals(2, results.size());
        verify(restrictionRepository, times(1)).findByCompanyId(1L);
    }

    @Test
    void testUpdateRestriction() {
        Company company = new Company();
        company.setId(1L);

        Restriction restriction = new Restriction();
        restriction.setId(1L);
        restriction.setModelType("gpt-5-mini");
        restriction.setUsageLimit(1000);
        restriction.setCompany(company);

        RestrictionDTO dto = new RestrictionDTO();
        dto.setModelType("gpt-5-mini");
        dto.setUsageLimit(2000);

        when(restrictionRepository.findById(1L)).thenReturn(Optional.of(restriction));
        when(restrictionRepository.save(any(Restriction.class))).thenReturn(restriction);

        RestrictionDTO result = restrictionService.updateRestriction(1L, dto);

        assertNotNull(result);
        assertEquals(2000, result.getUsageLimit());
        assertEquals(1L, result.getCompanyId());
    }

    @Test
    void testDeleteRestriction() {
        doNothing().when(restrictionRepository).deleteById(1L);

        restrictionService.deleteRestriction(1L);

        verify(restrictionRepository, times(1)).deleteById(1L);
    }
}
