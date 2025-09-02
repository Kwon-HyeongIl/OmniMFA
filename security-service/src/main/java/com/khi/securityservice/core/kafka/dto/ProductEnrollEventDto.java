package com.khi.securityservice.core.kafka.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductEnrollEventDto {

    private Long userId;

    private String productName;
    private String productDescription;

    private String clientId;
    private String hashedClientSecret;
}
