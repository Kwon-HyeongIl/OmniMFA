package com.khi.securityservice.core.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.securityservice.core.entity.domain.ProductEntity;
import com.khi.securityservice.core.entity.domain.ProductSecretEntity;
import com.khi.securityservice.core.entity.domain.UserEntity;
import com.khi.securityservice.core.kafka.dto.ProductEnrollEventDto;
import com.khi.securityservice.core.repository.ProductRepository;
import com.khi.securityservice.core.repository.ProductSecretRepository;
import com.khi.securityservice.core.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEventListener {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSecretRepository productSecretRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product-enroll-topic", groupId = "security-group")
    @Transactional
    public void handleEnrollProductEvent(String jsonPayload) {

        log.info("product-enroll-topic 이벤트 수신");

        ProductEnrollEventDto eventDto;

        try {

            eventDto = objectMapper.readValue(jsonPayload, ProductEnrollEventDto.class);

        } catch(JsonProcessingException e) {

            throw new RuntimeException("json 디코딩 실패", e);
        }

        UserEntity user = userRepository.findById(eventDto.getUid())
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않음, 요청된 uid: " + eventDto.getUid()));

        ProductEntity product = new ProductEntity();
        product.setUser(user);
        product.setProductName(eventDto.getProductName());
        product.setProductDescription(eventDto.getProductDescription());
        ProductEntity savedProduct = productRepository.save(product);

        ProductSecretEntity productSecret = new ProductSecretEntity();
        productSecret.setClientId(eventDto.getClientId());
        productSecret.setClientSecret(eventDto.getHashedClientSecret());
        productSecret.setProduct(savedProduct);
        productSecretRepository.save(productSecret);
    }
}