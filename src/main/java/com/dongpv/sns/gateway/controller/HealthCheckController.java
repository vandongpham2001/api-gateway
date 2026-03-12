package com.dongpv.sns.gateway.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dongpv.sns.gateway.dto.ApiResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthCheckController {

    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("service", "api-gateway");
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", Instant.now().toString());

        return ApiResponse.<Map<String, Object>>builder()
                .code(200)
                .status(true)
                .data(healthStatus)
                .build();
    }
}
