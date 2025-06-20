package com.example.demo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActuatorJob {

    private final RestTemplate restTemplate;

    @Value("${actuate-job.url}")
    private String url;

    @Scheduled(fixedDelay = 60000)
    public void actuate() {
        log.info("Start actuate app");
        try {
            restTemplate.getForObject(url,  Map.class);
        } catch (Exception e) {
            log.info("Expected exception");
        }
        log.info("Finish actuation");
    }
}
