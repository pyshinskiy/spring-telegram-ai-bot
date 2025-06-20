package com.example.demo.service;

import com.example.demo.dto.UpdateKeyRequest;
import com.example.demo.util.CryptoUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenRouterKeyService {

    private final String PATCH_URL = "https://openrouter.ai/api/v1/keys";

    @Value("${open-router.provisioning-key}")
    private String provisioningKey;

    @Getter
    @Value("${open-router.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public void enable() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + provisioningKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UpdateKeyRequest> request = new HttpEntity<>(new UpdateKeyRequest(false), headers);

        restTemplate.exchange(PATCH_URL + "/" + CryptoUtil.sha256(apiKey), HttpMethod.PATCH, request, Map.class);
    }
}
