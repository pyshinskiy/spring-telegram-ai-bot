package com.example.demo.dto;

public record ResponseMessage(
        String role,
        String content,
        Object refusal,
        Object reasoning
) {}
