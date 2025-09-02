package com.khi.securityservice.core.kafka.listener;

import com.khi.securityservice.core.entity.domain.UserEntity;
import com.khi.securityservice.core.kafka.dto.ProductEnrollEventDto;
import com.khi.securityservice.core.repository.ProductRepository;
import com.khi.securityservice.core.repository.ProductSecretRepository;
import com.khi.securityservice.core.repository.UserRepository;
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

    @KafkaListener(topics = "product-enroll-topic", groupId = "security-group")
    @Transactional
    public void handleEnrollProductEvent(ProductEnrollEventDto eventDto) {

        log.info("product-enroll-topic 이벤트 수신");

        UserEntity user = userRepository.findByLoginId(eventDto.g)
    }
}

UserEntity user = userRepository.findById(event.getUserId())
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

// 2. ProductEntity 생성 및 저장
ProductEntity product = new ProductEntity();
            product.setUser(user);
            product.setProductName(event.getProductName());
        product.setProductUrl(event.getProductUrl());
        product.setProductDescription(event.getProductDescription());
ProductEntity savedProduct = productRepository.save(product);

// 3. ProductSecretEntity 생성 및 저장
ProductSecretEntity secret = new ProductSecretEntity();
            secret.setClientId(event.getClientId());
        secret.setClientSecret(event.getHashedClientSecret()); // 해싱된 값 저장
        secret.setProduct(savedProduct);
            productSecretRepository.save(secret);