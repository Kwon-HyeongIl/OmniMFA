package com.khi.onboardingservice.content.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.onboardingservice.content.dto.kafka.ProductEnrollEventDto;
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

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_NAME = "product-enroll-topic";

    public EnrollResponseDto enrollProduct(EnrollRequestDto requestDto, Long uid) {

        String clientId = UUID.randomUUID().toString();
        String clientSecret = RandomStringUtils.randomAlphanumeric(20);

        String hashedClientSecret = passwordEncoder.encode(clientSecret);

        ProductEnrollEventDto event = ProductEnrollEventDto.builder()
                .uid(uid)
                .productName(requestDto.getProductName())
                .productDescription(requestDto.getProductDescription())
                .clientId(clientId)
                .hashedClientSecret(hashedClientSecret)
                .build();

        try {

            String jsonPayload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC_NAME, jsonPayload);

        } catch(JsonProcessingException e) {

            throw new RuntimeException("json 인코딩 실패", e);
        }

        return new EnrollResponseDto(clientId, clientSecret);
    }
}