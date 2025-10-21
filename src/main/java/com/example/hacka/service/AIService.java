package com.example.hacka.service;

import com.example.hacka.dto.AIRequestDTO;
import com.example.hacka.dto.AIResponseDTO;
import com.example.hacka.entity.Request;
import com.example.hacka.entity.User;
import com.example.hacka.entity.UserLimit;
import com.example.hacka.repository.RequestRepository;
import com.example.hacka.repository.UserLimitRepository;
import com.example.hacka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AIService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLimitRepository userLimitRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private GitHubModelsService gitHubModelsService;

    @Transactional
    public AIResponseDTO processRequest(String username, AIRequestDTO requestDTO) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow();

        List<UserLimit> limits = userLimitRepository.findByUserId(user.getId());
        UserLimit applicableLimit = limits.stream()
                .filter(l -> l.getModelType().equals(requestDTO.getModel()))
                .findFirst()
                .orElse(null);

        if (applicableLimit != null) {
            LocalDateTime windowStart = LocalDateTime.now().minusMinutes(applicableLimit.getTimeWindowMinutes());
            List<Request> recentRequests = requestRepository.findByUserIdAndCreatedAtAfter(user.getId(), windowStart);

            long requestCount = recentRequests.stream()
                    .filter(r -> r.getModelUsed().equals(requestDTO.getModel()))
                    .count();

            if (applicableLimit.getRequestLimitPerWindow() != null &&
                    requestCount >= applicableLimit.getRequestLimitPerWindow()) {
                throw new Exception("Request limit exceeded");
            }

            int tokensUsed = recentRequests.stream()
                    .filter(r -> r.getModelUsed().equals(requestDTO.getModel()))
                    .mapToInt(r -> r.getTokensConsumed() != null ? r.getTokensConsumed() : 0)
                    .sum();

            if (applicableLimit.getTokenLimitPerWindow() != null &&
                    tokensUsed >= applicableLimit.getTokenLimitPerWindow()) {
                throw new Exception("Token limit exceeded");
            }
        }

        AIResponseDTO response = gitHubModelsService.callModel(requestDTO);

        Request request = new Request();
        request.setUser(user);
        request.setQueryText(requestDTO.getMessage());
        request.setResponse(response.getResponse());
        request.setTokensConsumed(response.getTokensUsed());
        request.setModelUsed(requestDTO.getModel());
        request.setSuccess(true);
        requestRepository.save(request);

        return response;
    }

    public List<Request> getUserHistory(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return requestRepository.findByUserId(user.getId());
    }
}
