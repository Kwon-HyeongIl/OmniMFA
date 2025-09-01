package com.khi.securityservice.core.repository;

import com.khi.securityservice.core.entity.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
