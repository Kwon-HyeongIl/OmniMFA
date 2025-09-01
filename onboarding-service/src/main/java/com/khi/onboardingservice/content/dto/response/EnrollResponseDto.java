package com.khi.onboardingservice.content.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnrollResponseDto {

    private String clientId;
    private String clientSecret; // 해시되지 않은 원본 키
}
