package com.khi.onboardingservice.content.service;

import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.dto.response.EnrollResponseDto;
import com.khi.onboardingservice.content.entity.ProductEntity;
import com.khi.onboardingservice.content.entity.ProductSecretEntity;
import com.khi.onboardingservice.content.repository.ProductRepository;
import com.khi.onboardingservice.content.repository.ProductSecretRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnBoardingService {

    private final ProductRepository productRepository;
    private final ProductSecretRepository productSecretRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public EnrollResponseDto enrollProduct(EnrollRequestDto requestDto, Long uid) {

        ProductEntity product = new ProductEntity();
        product.setUid(uid);
        product.setProductName(requestDto.getProductName());
        product.setProductDescription(requestDto.getProductDescription());
        ProductEntity savedProduct = productRepository.save(product);

        String clientId = UUID.randomUUID().toString();
        String clientSecret = RandomStringUtils.randomAlphanumeric(20);
        String hashedClientSecret = passwordEncoder.encode(clientSecret);

        ProductSecretEntity productSecret = new ProductSecretEntity();
        productSecret.setClientId(clientId);
        productSecret.setClientSecret(hashedClientSecret);
        productSecret.setProduct(savedProduct);
        productSecretRepository.save(productSecret);

        return new EnrollResponseDto(clientId, clientSecret);
    }
}