package com.khi.apigatewayservice.security.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.apigatewayservice.security.entity.ProductSecretEntity;
import com.khi.apigatewayservice.security.dto.kafka.ProductEnrollEventDto;
import com.khi.apigatewayservice.security.repository.ProductSecretRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProductEventListener {

    private final ProductSecretRepository productSecretRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product-enroll-topic", groupId = "product-group")
    @Transactional
    public void handleProductTotpEnrollEvent(String jsonPayload) {

        log.info("product-enroll-topic 이벤트 수신");

        ProductEnrollEventDto enrollEvent;

        try {

            enrollEvent = objectMapper.readValue(jsonPayload, ProductEnrollEventDto.class);

        } catch(JsonProcessingException e) {

            throw new RuntimeException("json 디코딩 실패", e);
        }

        ProductSecretEntity totpSecret = new ProductSecretEntity();
        totpSecret.setProductId(enrollEvent.getProductId());
        totpSecret.setProductClientId(enrollEvent.getProductClientId());
        totpSecret.setProductHashedClientSecret(enrollEvent.getHashedProductClientSecret());
        productSecretRepository.save(totpSecret);

        log.info("ProductSecretEntity 저장 완료");
    }
}
