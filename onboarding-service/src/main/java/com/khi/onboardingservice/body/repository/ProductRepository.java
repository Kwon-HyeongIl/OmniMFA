package com.khi.onboardingservice.body.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khi.onboardingservice.body.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
}