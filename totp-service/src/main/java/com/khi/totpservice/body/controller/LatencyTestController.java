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

import com.khi.product.grpc.ProductGrpcServiceGrpc;
import com.khi.product.grpc.ProductRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Slf4j
@RestController
@RequestMapping("/totp/test")
@RequiredArgsConstructor
public class LatencyTestController {

    private final OnboardingFeignClient onboardingFeignClient;
    private final MeterRegistry meterRegistry;

    @GrpcClient("onboarding-service")
    private ProductGrpcServiceGrpc.ProductGrpcServiceBlockingStub productGrpcServiceBlockingStub;

    private static final int CONCURRENCY = 100; // 동시 요청 사용자 수
    private static final long TEST_DURATION_SECONDS = 10; // 테스트 지속 시간

    @PostMapping("/rest/setup")
    public String latencyTestWithRest(@RequestHeader("Product-Id") String productId) throws InterruptedException {

        Timer timer = Timer.builder("totp.loadtest.latency")
                .tag("type", "개선 전 (REST 방식)")
                .publishPercentileHistogram()
                .register(meterRegistry);

        java.util.concurrent.atomic.AtomicLong totalRequests = new java.util.concurrent.atomic.AtomicLong(0);
        long endTime = System.currentTimeMillis() + (TEST_DURATION_SECONDS * 1000);

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(CONCURRENCY);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(CONCURRENCY);

        long startTimeGlobal = System.nanoTime();

        for (int i = 0; i < CONCURRENCY; i++) {
            executor.submit(() -> {
                try {
                    while (System.currentTimeMillis() < endTime) {
                        long start = System.nanoTime();
                        try {
                            onboardingFeignClient.getProductNameByProductId(productId).orElse("Unknown");
                            long end = System.nanoTime();
                            timer.record(end - start, TimeUnit.NANOSECONDS);
                            totalRequests.incrementAndGet();
                        } catch (Exception e) {
                            log.error("REST request failed", e);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTimeGlobal = System.nanoTime();
        executor.shutdown();

        double totalSeconds = (endTimeGlobal - startTimeGlobal) / 1_000_000_000.0;
        long totalCount = totalRequests.get();
        double rps = totalCount / totalSeconds;
        double avgLatencyMs = timer.mean(TimeUnit.MILLISECONDS);

        log.info("REST Load Test Completed. RPS: {}, Avg: {} ms, Total: {}", String.format("%.2f", rps),
                String.format("%.4f", avgLatencyMs), totalCount);
        return String.format("평균 지연시간: %.4f ms", avgLatencyMs);
    }

    @PostMapping("/grpc/setup")
    public String latencyTestWithGrpc(@RequestHeader("Product-Id") String productId) throws InterruptedException {

        ProductRequest grpcRequest = ProductRequest.newBuilder().setProductId(productId).build();

        Timer timer = Timer.builder("totp.loadtest.latency")
                .tag("type", "개선 후 (gRPC 방식)")
                .publishPercentileHistogram()
                .register(meterRegistry);

        java.util.concurrent.atomic.AtomicLong totalRequests = new java.util.concurrent.atomic.AtomicLong(0);
        long endTime = System.currentTimeMillis() + (TEST_DURATION_SECONDS * 1000);

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(CONCURRENCY);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(CONCURRENCY);

        long startTimeGlobal = System.nanoTime();

        for (int i = 0; i < CONCURRENCY; i++) {
            executor.submit(() -> {
                try {
                    while (System.currentTimeMillis() < endTime) {
                        long start = System.nanoTime();
                        try {
                            productGrpcServiceBlockingStub.getProductName(grpcRequest).getProductName();
                            long end = System.nanoTime();
                            timer.record(end - start, TimeUnit.NANOSECONDS);
                            totalRequests.incrementAndGet();
                        } catch (Exception e) {
                            log.error("gRPC request failed", e);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTimeGlobal = System.nanoTime();
        executor.shutdown();

        double totalSeconds = (endTimeGlobal - startTimeGlobal) / 1_000_000_000.0;
        long totalCount = totalRequests.get();
        double rps = totalCount / totalSeconds;
        double avgLatencyMs = timer.mean(TimeUnit.MILLISECONDS);

        log.info("gRPC Load Test Completed. RPS: {}, Avg: {} ms, Total: {}", String.format("%.2f", rps),
                String.format("%.4f", avgLatencyMs), totalCount);
        return String.format("평균 지연시간: %.4f ms", avgLatencyMs);
    }
}
