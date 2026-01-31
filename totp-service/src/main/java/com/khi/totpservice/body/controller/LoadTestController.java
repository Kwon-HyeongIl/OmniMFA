package com.khi.totpservice.body.controller;

import com.khi.totpservice.client.OnboardingFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
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
    private final MeterRegistry meterRegistry;

    @net.devh.boot.grpc.client.inject.GrpcClient("onboarding-service")
    private com.khi.product.grpc.ProductGrpcServiceGrpc.ProductGrpcServiceBlockingStub productGrpcServiceBlockingStub;

    @PostMapping("/rest/setup")
    public String latencyTestWithRest(@RequestHeader("Product-Id") String productId) {
        StringBuilder result = new StringBuilder("REST (Feign) Latency Test (100,000 iterations):\n");
        long totalDuration = 0;

        // Warm-up (첫 요청은 초기화 비용 때문에 느리므로 제외)
        onboardingFeignClient.getProductNameByProductId(productId).orElse("Unknown");
        log.info("REST Warm-up complete");

        Timer timer = Timer.builder("totp.loadtest.latency")
                .tag("type", "rest")
                .publishPercentileHistogram()
                .register(meterRegistry);

        for (int i = 1; i <= 100000; i++) {
            long startTime = System.nanoTime();
            String productName = onboardingFeignClient.getProductNameByProductId(productId).orElse("Unknown");
            long endTime = System.nanoTime();

            long durationNs = endTime - startTime;
            timer.record(durationNs, TimeUnit.NANOSECONDS);

            totalDuration += durationNs;
        }

        double average = (totalDuration / 100000.0) / 1_000_000.0;
        log.info("REST Average Latency (excl. warmup): {} ms", average);
        result.append(String.format("평균 (100,000 회): %.4f ms", average));

        return result.toString();
    }

    @PostMapping("/grpc/setup")
    public String latencyTestWithGrpc(@RequestHeader("Product-Id") String productId) {
        StringBuilder result = new StringBuilder("gRPC Latency Test (100,000 iterations):\n");
        long totalDuration = 0;

        com.khi.product.grpc.ProductRequest grpcRequest = com.khi.product.grpc.ProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        // Warm-up
        productGrpcServiceBlockingStub.getProductName(grpcRequest).getProductName();
        log.info("gRPC Warm-up complete");

        Timer timer = Timer.builder("totp.loadtest.latency")
                .tag("type", "grpc")
                .publishPercentileHistogram()
                .register(meterRegistry);

        for (int i = 1; i <= 100000; i++) {
            long startTime = System.nanoTime();
            String productName = productGrpcServiceBlockingStub.getProductName(grpcRequest).getProductName();
            long endTime = System.nanoTime();

            long durationNs = endTime - startTime;
            timer.record(durationNs, TimeUnit.NANOSECONDS);

            totalDuration += durationNs;
        }

        double average = (totalDuration / 100000.0) / 1_000_000.0;
        log.info("gRPC Average Latency (excl. warmup): {} ms", average);
        result.append(String.format("평균 (100,000 회): %.4f ms", average));

        return result.toString();
    }
}
