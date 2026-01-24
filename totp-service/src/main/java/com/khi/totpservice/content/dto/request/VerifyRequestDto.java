package com.khi.totpservice.content.dto.request;

import lombok.Data;

@Data
public class VerifyRequestDto {

    private String productClientUid;
    private String code;
}
