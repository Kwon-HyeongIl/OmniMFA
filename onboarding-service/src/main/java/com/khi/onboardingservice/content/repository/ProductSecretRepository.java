package com.khi.onboardingservice.content.repository;

import com.khi.onboardingservice.content.entity.ProductSecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSecretRepository extends JpaRepository<ProductSecretEntity, String> {
}
