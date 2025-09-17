package com.khi.onboardingservice.content.controller;

import com.khi.onboardingservice.common.api.ApiResponse;
import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.dto.response.EnrollResponseDto;
import com.khi.onboardingservice.content.service.ProductService;
import com.khi.onboardingservice.security.principal.SecurityUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<?>> enrollProduct(@RequestBody EnrollRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {

        SecurityUserPrincipal userPrincipal = (SecurityUserPrincipal) userDetails;

        Long uid = Long.valueOf(userPrincipal.getUsername());

        log.info("인증된 uid: {}", uid);

        EnrollResponseDto reponseDto = productService.enrollProduct(requestDto, uid);

        return ResponseEntity.ok(ApiResponse.success(reponseDto));
    }

    @PostMapping("/test")
    public String test() {
        return "ok";
    }
}
