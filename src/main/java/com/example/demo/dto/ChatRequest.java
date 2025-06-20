package com.example.demo.dto;

public record ChatRequest(
        String model,
        Message[] messages
) {
}
