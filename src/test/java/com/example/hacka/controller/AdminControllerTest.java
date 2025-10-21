package com.example.hacka.controller;

import com.example.hacka.dto.CompanyDTO;
import com.example.hacka.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @Test
    @WithMockUser(authorities = "ROLE_SPARKY_ADMIN")
    void testCreateCompany() throws Exception {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(1L);
        dto.setName("Test Corp");
        dto.setRuc("20123456789");

        when(companyService.createCompany(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/admin/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Corp\",\"ruc\":\"20123456789\",\"adminUsername\":\"admin\",\"adminPassword\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Corp"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_SPARKY_ADMIN")
    void testGetAllCompanies() throws Exception {
        CompanyDTO dto1 = new CompanyDTO();
        dto1.setId(1L);
        dto1.setName("Company 1");

        CompanyDTO dto2 = new CompanyDTO();
        dto2.setId(2L);
        dto2.setName("Company 2");

        when(companyService.getAllCompanies()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/admin/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Company 1"))
                .andExpect(jsonPath("$[1].name").value("Company 2"));
    }

    @Test
    void testCreateCompanyUnauthorized() throws Exception {
        mockMvc.perform(post("/api/admin/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Corp\"}"))
                .andExpect(status().isForbidden());
    }
}
