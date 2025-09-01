package com.khi.onboardingservice.content.dto.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductEnrollEventDto {

    private Long userId;

    private String productName;
    private String productUrl;
    private String productDescription;

    private String clientId;
    private String hashedClientSecret;
}
