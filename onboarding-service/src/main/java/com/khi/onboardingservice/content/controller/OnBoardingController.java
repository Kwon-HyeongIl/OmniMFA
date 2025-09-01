package com.khi.onboardingservice.content.controller;

import com.khi.onboardingservice.common.api.ApiResponse;
import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.service.OnBoardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboard")
public class OnBoardingController {

    private final OnBoardingService onBoardingService;

//    @PostMapping("/enroll")
//    public ResponseEntity<ApiResponse<?>> enrollProduct(@RequestBody EnrollRequestDto requestDto) {
//
//
//    }
}
