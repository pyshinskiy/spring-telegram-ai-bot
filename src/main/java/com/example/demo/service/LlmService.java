package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class LlmService {

    private final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

    @Value("${open-router.api-key}")
    private String apiKey; // 🔒 Замени на свой API-ключ

    private final RestTemplate restTemplate = new RestTemplate();

    public String queryModel(String model, String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "user", "content", userMessage)
                }
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENROUTER_URL, entity, Map.class);
            var choices = (java.util.List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message.get("content").toString();
            }
            return "⚠️ Empty response";
        } catch (Exception e) {
            log.error("Error occurred during requesting llm model {}", model, e);
            return "❌ Error during requesting model";
        }
    }
}

