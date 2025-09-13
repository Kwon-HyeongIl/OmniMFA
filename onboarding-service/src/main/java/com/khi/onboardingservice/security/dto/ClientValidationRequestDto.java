package com.khi.onboardingservice.security.dto;

import lombok.Data;

@Data
public class ClientValidationRequestDto {

    private String clientId;
    private String clientSecret;
}
