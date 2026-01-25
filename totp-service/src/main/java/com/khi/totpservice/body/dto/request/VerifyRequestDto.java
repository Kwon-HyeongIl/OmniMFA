package com.khi.totpservice.body.dto.request;

import lombok.Data;

@Data
public class VerifyRequestDto {

    private String productClientUid;
    private String code;
}
