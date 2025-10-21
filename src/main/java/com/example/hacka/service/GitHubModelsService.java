package com.example.hacka.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.example.hacka.dto.AIRequestDTO;
import com.example.hacka.dto.AIResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubModelsService {

    @Value("${github.models.token:}")
    private String githubToken;

    @Value("${github.models.endpoint:https://models.inference.ai.azure.com}")
    private String endpoint;

    public AIResponseDTO callModel(AIRequestDTO requestDTO) {
        AIResponseDTO response = new AIResponseDTO();

        try {
            // Si no hay token configurado, usar respuesta simulada
            if (githubToken == null || githubToken.isEmpty()) {
                response.setResponse("Respuesta simulada del modelo: " + requestDTO.getModel() +
                        ". Para usar modelos reales, configure github.models.token en application.properties");
                response.setTokensUsed(100);
                response.setModel(requestDTO.getModel());
                return response;
            }

            // Integración real con GitHub Models
            ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                    .credential(new AzureKeyCredential(githubToken))
                    .endpoint(endpoint)
                    .buildClient();

            List<ChatRequestMessage> messages = new ArrayList<>();
            messages.add(new ChatRequestUserMessage(requestDTO.getMessage()));

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel(getModelName(requestDTO.getModel()));
            options.setTemperature(0.8);
            options.setMaxTokens(4096);

            ChatCompletions completions = client.complete(options);

            ChatChoice choice = completions.getChoices().get(0);
            CompletionsUsage usage = completions.getUsage();

            response.setResponse(choice.getMessage().getContent());
            response.setTokensUsed(usage.getTotalTokens());
            response.setModel(requestDTO.getModel());

        } catch (Exception e) {
            response.setResponse("Error al llamar al modelo: " + e.getMessage());
            response.setTokensUsed(0);
            response.setModel(requestDTO.getModel());
        }

        return response;
    }

    private String getModelName(String modelType) {
        return switch (modelType.toLowerCase()) {
            case "gpt-5-mini" -> "gpt-5-mini";
            case "meta-llama" -> "meta-llama-3.1-405b-instruct";
            case "deepseek" -> "deepseek-r1";
            case "openai" -> "gpt-5-mini";
            default -> "gpt-5-mini";
        };
    }
}