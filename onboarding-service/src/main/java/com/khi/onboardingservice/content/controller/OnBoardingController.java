package com.khi.onboardingservice.content.controller;

import com.khi.onboardingservice.common.api.ApiResponse;
import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.dto.response.EnrollResponseDto;
import com.khi.onboardingservice.content.service.OnBoardingService;
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
@RequestMapping("/onboard")
public class OnBoardingController {

    private final OnBoardingService onBoardingService;

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<?>> enrollProduct(@RequestBody EnrollRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {

        SecurityUserPrincipal userPrincipal = (SecurityUserPrincipal) userDetails;

        Long userId = Long.valueOf(userPrincipal.getUsername());

        log.info("인증된 userId: {}", userId);

        EnrollResponseDto reponseDto = onBoardingService.enrollProduct(requestDto, userId);

        return ResponseEntity.ok(ApiResponse.success(reponseDto));
    }
}
