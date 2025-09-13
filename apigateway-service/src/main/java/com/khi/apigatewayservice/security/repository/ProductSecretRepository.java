package com.khi.apigatewayservice.security.repository;

import com.khi.apigatewayservice.security.entity.ProductSecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSecretRepository extends JpaRepository<ProductSecretEntity, String> {

    Optional<ProductSecretEntity> findByProductClientId(String clientId);
}
