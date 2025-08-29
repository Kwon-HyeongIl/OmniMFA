package com.khi.securityservice.gateway.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDto {

    private String loginId;
    private String password;
}
