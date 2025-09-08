package com.khi.totpservice.content.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TotpClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId; // Product의 clientId
    private String clientUId; // 고객 서비스 사용자 uid

    private String totpSecretKey;

    private boolean isEnabled;
}
