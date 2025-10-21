package com.example.hacka.controller;

import com.example.hacka.dto.AIRequestDTO;
import com.example.hacka.dto.AIResponseDTO;
import com.example.hacka.service.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AIService aiService;

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testChatEndpoint() throws Exception {
        AIResponseDTO response = new AIResponseDTO();
        response.setResponse("Test response");
        response.setTokensUsed(100);
        response.setModel("gpt-5-mini");

        when(aiService.processRequest(anyString(), any(AIRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Hello\",\"model\":\"gpt-5-mini\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Test response"))
                .andExpect(jsonPath("$.tokensUsed").value(100));
    }

    @Test
    void testChatEndpointUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Hello\",\"model\":\"gpt-5-mini\"}"))
                .andExpect(status().isForbidden());
    }
}
