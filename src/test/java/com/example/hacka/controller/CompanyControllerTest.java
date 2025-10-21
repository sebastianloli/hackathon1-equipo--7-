package com.example.hacka.controller;

import com.example.hacka.dto.RestrictionDTO;
import com.example.hacka.dto.UserDTO;
import com.example.hacka.service.RestrictionService;
import com.example.hacka.service.UserService;
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
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestrictionService restrictionService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(authorities = "ROLE_COMPANY_ADMIN")
    void testCreateRestriction() throws Exception {
        RestrictionDTO dto = new RestrictionDTO();
        dto.setId(1L);
        dto.setModelType("gpt-5-mini");
        dto.setUsageLimit(1000);

        when(restrictionService.createRestriction(any())).thenReturn(dto);

        mockMvc.perform(post("/api/company/restrictions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyId\":1,\"modelType\":\"gpt-5-mini\",\"usageLimit\":1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelType").value("gpt-5-mini"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_COMPANY_ADMIN")
    void testGetRestrictions() throws Exception {
        RestrictionDTO dto = new RestrictionDTO();
        dto.setId(1L);
        dto.setModelType("gpt-5-mini");

        when(restrictionService.getRestrictionsByCompany(1L)).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/company/restrictions?companyId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modelType").value("gpt-5-mini"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_COMPANY_ADMIN")
    void testCreateUser() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("testuser");

        when(userService.createUser(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/company/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"email\":\"test@test.com\",\"password\":\"pass\",\"companyId\":\"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testCreateRestrictionUnauthorized() throws Exception {
        mockMvc.perform(post("/api/company/restrictions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyId\":1,\"modelType\":\"gpt-5-mini\"}"))
                .andExpect(status().isForbidden());
    }
}
