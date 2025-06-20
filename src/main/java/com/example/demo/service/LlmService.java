package com.example.demo.service;

import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {

    private final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

    private final OpenRouterKeyService openRouterKeyService;

    private final RestTemplate restTemplate;

    public String queryModel(String model, String userMessage) {
        String apiKey = openRouterKeyService.getApiKey();

        try {
            return getAnswer(sendRequest(apiKey, model, userMessage));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                log.warn("Client error received ({}). Trying to re-enable key and retry...", e.getStatusCode());
                openRouterKeyService.enable();
                String newKey = openRouterKeyService.getApiKey();
                ChatCompletionResponse chatCompletionResponse = sendRequest(newKey, model, userMessage);
                return getAnswer(chatCompletionResponse);
            } else {
                log.error("HTTP error during LLM request: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Unexpected error during LLM request for model {}: {}", model, e.getMessage(), e);
        }

        return "❌ Error during requesting model";
    }

    private ChatCompletionResponse sendRequest(String apiKey, String model, String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);  // без "Bearer " в кавычках

        ChatRequest requestBody = new ChatRequest(
                model,
                new Message[]{ new Message("user", userMessage) }
        );

        HttpEntity<ChatRequest> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.postForEntity(
                OPENROUTER_URL, entity, ChatCompletionResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Invalid response from OpenRouter: " + response.getStatusCode());
        }

        return response.getBody();
    }

    private String getAnswer(ChatCompletionResponse chatResponse) {
        if (chatResponse == null || chatResponse.choices() == null || chatResponse.choices().isEmpty()) {
            return "⚠️ Empty or invalid response from model";
        }

       return chatResponse.choices().getFirst().message().content();

    }
}

