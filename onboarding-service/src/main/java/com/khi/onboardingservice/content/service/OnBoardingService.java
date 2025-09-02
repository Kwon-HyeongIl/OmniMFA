package com.khi.onboardingservice.content.service;

import com.khi.onboardingservice.content.kafka.ProductEnrollEventDto;
import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.dto.response.EnrollResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnBoardingService {

    private final KafkaTemplate<String, ProductEnrollEventDto> kafkaTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String TOPIC_NAME = "product-enroll-topic";

    public EnrollResponseDto enrollProduct(EnrollRequestDto requestDto, Long userId) {

        String clientId = UUID.randomUUID().toString();
        String clientSecret = RandomStringUtils.randomAlphanumeric(20);

        String hashedClientSecret = passwordEncoder.encode(clientSecret);

        ProductEnrollEventDto event = ProductEnrollEventDto.builder()
                .userId(userId)
                .productName(requestDto.getProductName())
                .productDescription(requestDto.getProductDescription())
                .clientId(clientId)
                .hashedClientSecret(hashedClientSecret)
                .build();

        kafkaTemplate.send(TOPIC_NAME, event);

        return new EnrollResponseDto(clientId, clientSecret);
    }
}