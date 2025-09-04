package com.khi.securityservice.core.entity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class ProductSecretEntity {

    @Id
    private String clientId;

    @OneToOne
    private ProductEntity product;

    private String clientSecret;
}
