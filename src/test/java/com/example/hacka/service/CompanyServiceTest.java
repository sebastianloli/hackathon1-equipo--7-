package com.example.hacka.service;

import com.example.hacka.dto.CompanyDTO;
import com.example.hacka.entity.Company;
import com.example.hacka.repository.CompanyRepository;
import com.example.hacka.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void testCreateCompany() {
        CompanyDTO dto = new CompanyDTO();
        dto.setName("Test Corp");
        dto.setRuc("20123456789");

        Company company = new Company();
        company.setId(1L);
        company.setName("Test Corp");
        company.setRuc("20123456789");

        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        CompanyDTO result = companyService.createCompany(dto, "admin", "pass");

        assertNotNull(result);
        assertEquals("Test Corp", result.getName());
        verify(companyRepository, times(1)).save(any(Company.class));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testGetAllCompanies() {
        Company company1 = new Company();
        company1.setId(1L);
        company1.setName("Company 1");

        Company company2 = new Company();
        company2.setId(2L);
        company2.setName("Company 2");

        when(companyRepository.findAll()).thenReturn(Arrays.asList(company1, company2));

        List<CompanyDTO> results = companyService.getAllCompanies();

        assertEquals(2, results.size());
        verify(companyRepository, times(1)).findAll();
    }
}
