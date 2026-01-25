package com.khi.onboardingservice.body.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProductEntity {

    @Id
    private String id;

    private String hashedProductSecret;

    private Long uid;

    private String productName;
    private String productDescription;
}
