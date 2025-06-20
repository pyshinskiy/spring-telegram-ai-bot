package com.example.demo.dto;

import java.util.List;

public record ChatCompletionResponse(
        String id,
        String provider,
        String model,
        String object,
        long created,
        List<Choice> choices
) {}
