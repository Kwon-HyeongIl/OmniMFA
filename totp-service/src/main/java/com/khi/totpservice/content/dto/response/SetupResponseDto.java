package com.khi.totpservice.content.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetupResponseDto {

    private String qrCodeDataUri;
}
