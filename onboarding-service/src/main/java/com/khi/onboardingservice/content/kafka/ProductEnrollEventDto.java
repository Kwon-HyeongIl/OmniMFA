package com.khi.onboardingservice.content.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductEnrollEventDto {

    private Long uid;

    private String productName;
    private String productDescription;

    private String clientId;
    private String hashedClientSecret;
}
