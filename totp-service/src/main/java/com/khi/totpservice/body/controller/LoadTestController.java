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
    public String latencyTestWithRest(@RequestHeader("Product-Id") String productId) {
        StringBuilder result = new StringBuilder("REST 방식 지연 시간 테스트:\n");
        long totalDuration = 0;

        // Warm-up (첫 요청은 초기화 비용 때문에 느리므로 제외)
        onboardingFeignClient.getProductNameByProductId(productId).orElse("Unknown");
        log.info("REST Warm-up complete");

        for (int i = 1; i <= 100; i++) {
            long startTime = System.nanoTime();
            String productName = onboardingFeignClient.getProductNameByProductId(productId).orElse("Unknown");
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;
            totalDuration += durationMs;

            log.info("REST Iteration {}: {} ms", i, durationMs);
        }

        double average = totalDuration / 100.0;
        log.info("REST Average Latency (excl. warmup): {} ms", average);
        result.append(String.format("평균 (100 회): %.2f ms", average));

        return result.toString();
    }

    @PostMapping("/grpc/setup")
    public String latencyTestWithGrpc(@RequestHeader("Product-Id") String productId) {
        StringBuilder result = new StringBuilder("gRPC 방식 지연 시간 테스트:\n");
        long totalDuration = 0;

        com.khi.product.grpc.ProductRequest grpcRequest = com.khi.product.grpc.ProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        // Warm-up
        productGrpcServiceBlockingStub.getProductName(grpcRequest).getProductName();
        log.info("gRPC Warm-up complete");

        for (int i = 1; i <= 100; i++) {
            long startTime = System.nanoTime();
            String productName = productGrpcServiceBlockingStub.getProductName(grpcRequest).getProductName();
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;
            totalDuration += durationMs;

            log.info("gRPC Iteration {}: {} ms", i, durationMs);
        }

        double average = totalDuration / 100.0;
        log.info("gRPC Average Latency (excl. warmup): {} ms", average);
        result.append(String.format("평균 (100 회): %.2f ms", average));

        return result.toString();
    }
}
