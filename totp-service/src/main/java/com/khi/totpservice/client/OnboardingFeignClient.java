package com.khi.totpservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "onboarding-service", url = "http://onboarding-k8ssvc:80")
public interface OnboardingFeignClient {

    @GetMapping("/onboarding/product/internal/{productId}/name")
    Optional<String> getProductNameByProductId(@PathVariable("productId") String productId);
}
