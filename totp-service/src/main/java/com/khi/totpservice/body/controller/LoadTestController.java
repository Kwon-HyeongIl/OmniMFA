package com.khi.totpservice.body.controller;

import com.khi.totpservice.client.OnboardingFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/totp/test")
@RequiredArgsConstructor
public class LoadTestController {

    private final OnboardingFeignClient onboardingFeignClient;

    @GetMapping("/setup")
    public String checkLatency(@RequestHeader("Product-Id") String productId) {
        long startTime = System.nanoTime();

        String productName = onboardingFeignClient.getProductNameByProductId(productId).orElse("Unknown");

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        log.info("Feign Client Latency: {} ms for Product: {}", durationMs, productName);

        return String.format("Product: %s, Latency: %d ms", productName, durationMs);
    }
}
