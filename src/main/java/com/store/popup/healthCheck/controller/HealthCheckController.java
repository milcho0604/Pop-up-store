package com.store.popup.healthCheck.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Service is running smoothly");
        return status;
    }
    @GetMapping("/health/check")
    public String healthCheck2() {
        return "Server oK";
    }
}
