package com.khi.onboardingservice.content.repository;

import com.khi.onboardingservice.content.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}