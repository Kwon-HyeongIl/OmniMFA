package com.khi.onboardingservice.content.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.onboardingservice.content.dto.kafka.ProductEnrollEventDto;
import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.dto.response.EnrollResponseDto;
import com.khi.onboardingservice.content.entity.ProductEntity;
import com.khi.onboardingservice.content.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProductRepository productRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_NAME = "product-enroll-topic";

    public EnrollResponseDto enrollProduct(EnrollRequestDto requestDto, Long uid) {

        String productClientId = UUID.randomUUID().toString();
        String productClientSecret = RandomStringUtils.randomAlphanumeric(20);
        String hashedProductClientSecret = bCryptPasswordEncoder.encode(productClientSecret);

        ProductEntity product = new ProductEntity();
        product.setUid(uid);
        product.setProductName(requestDto.getProductName());
        product.setProductDescription(requestDto.getProductDescription());
        product.setProductClientId(productClientId);
        ProductEntity savedProduct = productRepository.save(product);

        ProductEnrollEventDto enrollEvent = ProductEnrollEventDto.builder()
                .productId(savedProduct.getId())
                .productClientId(productClientId)
                .hashedProductClientSecret(hashedProductClientSecret)
                .build();

        try {

            String jsonPayload = objectMapper.writeValueAsString(enrollEvent);

            kafkaTemplate.send(TOPIC_NAME, jsonPayload);
        } catch(JsonProcessingException e) {

            throw new RuntimeException("json 인코딩 실패", e);
        }

        return new EnrollResponseDto(productClientId, productClientSecret);
    }
}