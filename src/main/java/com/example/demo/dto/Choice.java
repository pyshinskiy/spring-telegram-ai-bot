package com.example.demo.dto;

public record Choice(
        Object logprobs,
        String finish_reason,
        String native_finish_reason,
        int index,
        ResponseMessage message
) {}
