package com.khi.onboardingservice.content.controller;

import com.khi.onboardingservice.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding")
public class OnBoardingController {

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<?>> enroll() {

    }
}
