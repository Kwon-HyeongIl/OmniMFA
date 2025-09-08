package com.khi.onboardingservice.content.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long uid;

    private String productName;
    private String productDescription;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductSecretEntity productSecret;
}
