package com.example.demo.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OpenRouterClient {

    private final RestClient restClient;

    public OpenRouterClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer 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")
                .defaultHeader("HTTP-Referer", "https://example.com")
                .defaultHeader("X-Title", "MyTelegramBot")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String chat(String userMessage) {
        Map<String, Object> request = Map.of(
                "model", "openai/gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "user", "content", userMessage)
                )
        );

        Map<String, Object> response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("choices") == null) return "Нет ответа";

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices.isEmpty()) return "Пустой ответ";

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return message != null ? (String) message.get("content") : "Ошибка разбора ответа";
    }
}
