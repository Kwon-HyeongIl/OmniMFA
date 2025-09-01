package com.khi.onboardingservice.content.service;

import com.khi.onboardingservice.content.dto.kafka.ProductEnrollEventDto;
import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.dto.response.EnrollResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnBoardingService {

    private final KafkaTemplate<String, ProductEnrollEventDto> kafkaTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String TOPIC_NAME = "product-enroll-topic";

//    public EnrollResponseDto enrollProduct(EnrollRequestDto requestDto) {
//
//    }
}
