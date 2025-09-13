package com.khi.apigatewayservice.security.dto.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductEnrollEventDto {

    private Long productId;

    private String productClientId;
    private String hashedProductClientSecret;
}
