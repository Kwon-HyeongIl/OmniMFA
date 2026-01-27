package com.khi.onboardingservice.body.controller.rest;

import com.khi.onboardingservice.api.api.ApiResponse;
import com.khi.onboardingservice.body.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.body.dto.response.EnrollResponseDto;
import com.khi.onboardingservice.body.service.ProductService;
import com.khi.onboardingservice.security.principal.SecurityUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<?>> enrollProduct(@RequestBody EnrollRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        SecurityUserPrincipal userPrincipal = (SecurityUserPrincipal) userDetails;

        Long uid = Long.valueOf(userPrincipal.getUsername());

        log.info("인증된 uid: {}", uid);

        EnrollResponseDto reponseDto = productService.enrollProduct(requestDto, uid);

        return ResponseEntity.ok(ApiResponse.success(reponseDto));
    }

    // [Internal API]
    @GetMapping("/internal/{productId}/secret")
    public Optional<String> getHashedSecretByProductId(@PathVariable String productId) {

        log.info("Internal API - 제품 secret 조회 요청, productId: {}", productId);
        return productService.getHashedSecretByProductId(productId);
    }

    // [Internal API]
    @GetMapping("/internal/{productId}/name")
    public Optional<String> getProductNameByProductId(@PathVariable String productId) {

        log.info("Internal API - 제품 이름 조회 요청, productId: {}", productId);
        return productService.getProductNameByProductId(productId);
    }
}
