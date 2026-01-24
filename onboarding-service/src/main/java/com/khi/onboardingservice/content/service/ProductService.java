package com.khi.onboardingservice.content.service;

import com.khi.onboardingservice.content.dto.request.EnrollRequestDto;
import com.khi.onboardingservice.content.dto.response.EnrollResponseDto;
import com.khi.onboardingservice.content.entity.ProductEntity;
import com.khi.onboardingservice.content.repository.ProductAuthRedisRepository;
import com.khi.onboardingservice.content.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductAuthRedisRepository productAuthRedisRepository;
    private final ProductRepository productRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public EnrollResponseDto enrollProduct(EnrollRequestDto requestDto, Long uid) {

        String productId = UUID.randomUUID().toString();
        String productSecret = RandomStringUtils.randomAlphanumeric(20);
        String hashedProductSecret = bCryptPasswordEncoder.encode(productSecret);

        // DB에 Product 저장
        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setHashedProductSecret(hashedProductSecret);
        product.setUid(uid);
        product.setProductName(requestDto.getProductName());
        product.setProductDescription(requestDto.getProductDescription());
        productRepository.save(product);

        // Redis에 제품 인증 정보 저장
        productAuthRedisRepository.saveProductAuth(productId, hashedProductSecret);

        return new EnrollResponseDto(productId, productSecret);
    }
}