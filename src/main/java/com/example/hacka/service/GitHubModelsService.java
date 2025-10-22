package com.example.hacka.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.example.hacka.dto.SalesAggregates;
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

    @Value("${github.models.model.id:gpt-4o-mini}")
    private String modelId;

    public String generateSummary(SalesAggregates aggregates) {
        try {
            if (githubToken == null || githubToken.isEmpty()) {
                return generateMockSummary(aggregates);
            }

            ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                    .credential(new AzureKeyCredential(githubToken))
                    .endpoint(endpoint)
                    .buildClient();

            String prompt = String.format(
                    "Eres un analista que escribe resúmenes breves y claros para emails corporativos. " +
                            "Con estos datos: totalUnits=%d, totalRevenue=%.2f, topSku=%s, topBranch=%s. " +
                            "Escribe un resumen en español, claro y sin alucinaciones. Máximo 120 palabras. " +
                            "Debe mencionar al menos uno: unidades totales, SKU top, sucursal top, o total recaudado.",
                    aggregates.getTotalUnits(),
                    aggregates.getTotalRevenue(),
                    aggregates.getTopSku(),
                    aggregates.getTopBranch()
            );

            List<ChatRequestMessage> messages = new ArrayList<>();
            messages.add(new ChatRequestSystemMessage("Eres un analista que escribe resúmenes breves y claros para emails corporativos."));
            messages.add(new ChatRequestUserMessage(prompt));

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel(modelId);
            options.setMaxTokens(200);
            options.setTemperature(0.7);

            ChatCompletions completions = client.complete(options);
            return completions.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            System.out.println("Error calling GitHub Models: " + e.getMessage());
            return generateMockSummary(aggregates);
        }
    }

    private String generateMockSummary(SalesAggregates aggregates) {
        return String.format(
                "Esta semana vendimos %d unidades con un total de $%.2f en ingresos. " +
                        "El SKU más vendido fue %s y la sucursal con más ventas fue %s.",
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue(),
                aggregates.getTopSku(),
                aggregates.getTopBranch()
        );
    }
}