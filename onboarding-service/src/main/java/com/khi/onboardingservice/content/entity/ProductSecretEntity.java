package com.khi.onboardingservice.content.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductSecretEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String clientSecret;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
