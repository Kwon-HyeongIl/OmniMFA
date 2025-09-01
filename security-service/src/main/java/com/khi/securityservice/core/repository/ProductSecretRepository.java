package com.khi.securityservice.core.repository;

import com.khi.securityservice.core.entity.domain.ProductSecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSecretRepository extends JpaRepository<ProductSecretEntity, String> {
}
