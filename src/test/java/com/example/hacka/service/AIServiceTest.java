package com.example.hacka.service;

import com.example.hacka.dto.AIRequestDTO;
import com.example.hacka.dto.AIResponseDTO;
import com.example.hacka.entity.User;
import com.example.hacka.entity.UserLimit;
import com.example.hacka.repository.RequestRepository;
import com.example.hacka.repository.UserLimitRepository;
import com.example.hacka.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLimitRepository userLimitRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private GitHubModelsService gitHubModelsService;

    @InjectMocks
    private AIService aiService;

    @Test
    void testProcessRequestSuccess() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        AIRequestDTO requestDTO = new AIRequestDTO();
        requestDTO.setMessage("Hello");
        requestDTO.setModel("gpt-5-mini");

        AIResponseDTO responseDTO = new AIResponseDTO();
        responseDTO.setResponse("Hi there");
        responseDTO.setTokensUsed(50);
        responseDTO.setModel("gpt-5-mini");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userLimitRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(gitHubModelsService.callModel(any())).thenReturn(responseDTO);
        when(requestRepository.save(any())).thenReturn(null);

        AIResponseDTO result = aiService.processRequest("testuser", requestDTO);

        assertNotNull(result);
        assertEquals("Hi there", result.getResponse());
        assertEquals(50, result.getTokensUsed());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void testProcessRequestWithLimits() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserLimit limit = new UserLimit();
        limit.setModelType("gpt-5-mini");
        limit.setRequestLimitPerWindow(100);
        limit.setTokenLimitPerWindow(10000);
        limit.setTimeWindowMinutes(60);

        AIRequestDTO requestDTO = new AIRequestDTO();
        requestDTO.setMessage("Hello");
        requestDTO.setModel("gpt-5-mini");

        AIResponseDTO responseDTO = new AIResponseDTO();
        responseDTO.setResponse("Hi there");
        responseDTO.setTokensUsed(50);
        responseDTO.setModel("gpt-4");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userLimitRepository.findByUserId(1L)).thenReturn(java.util.List.of(limit));
        when(requestRepository.findByUserIdAndCreatedAtAfter(any(), any())).thenReturn(new ArrayList<>());
        when(gitHubModelsService.callModel(any())).thenReturn(responseDTO);
        when(requestRepository.save(any())).thenReturn(null);

        AIResponseDTO result = aiService.processRequest("testuser", requestDTO);

        assertNotNull(result);
        assertEquals("Hi there", result.getResponse());
    }
}
