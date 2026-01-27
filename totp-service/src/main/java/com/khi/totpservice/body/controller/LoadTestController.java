package com.khi.totpservice.body.controller;

import com.khi.totpservice.client.OnboardingFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/totp/test")
@RequiredArgsConstructor
public class LoadTestController {

    private final OnboardingFeignClient onboardingFeignClient;

    @net.devh.boot.grpc.client.inject.GrpcClient("onboarding-service")
    private com.khi.product.grpc.ProductGrpcServiceGrpc.ProductGrpcServiceBlockingStub productGrpcServiceBlockingStub;

    @PostMapping("/rest/setup")
    public String speedTestWithRest(@RequestHeader("Product-Id") String productId) {
        StringBuilder result = new StringBuilder("REST (Feign) Latency Test (10 iterations):\n");
        long totalDuration = 0;

        for (int i = 1; i <= 10; i++) {
            long startTime = System.nanoTime();
            String productName = onboardingFeignClient.getProductNameByProductId(productId).orElse("Unknown");
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;
            totalDuration += durationMs;

            log.info("REST Iteration {}: {} ms", i, durationMs);
            result.append(String.format("Iter %d: %d ms\n", i, durationMs));
        }

        double average = totalDuration / 10.0;
        log.info("REST Average Latency: {} ms", average);
        result.append(String.format("Average: %.2f ms", average));

        return result.toString();
    }

    @PostMapping("/grpc/setup")
    public String speedTestWithGrpc(@RequestHeader("Product-Id") String productId) {
        StringBuilder result = new StringBuilder("gRPC Latency Test (10 iterations):\n");
        long totalDuration = 0;

        com.khi.product.grpc.ProductRequest grpcRequest = com.khi.product.grpc.ProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        for (int i = 1; i <= 10; i++) {
            long startTime = System.nanoTime();
            String productName = productGrpcServiceBlockingStub.getProductName(grpcRequest).getProductName();
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;
            totalDuration += durationMs;

            log.info("gRPC Iteration {}: {} ms", i, durationMs);
            result.append(String.format("Iter %d: %d ms\n", i, durationMs));
        }

        double average = totalDuration / 10.0;
        log.info("gRPC Average Latency: {} ms", average);
        result.append(String.format("Average: %.2f ms", average));

        return result.toString();
    }
}
