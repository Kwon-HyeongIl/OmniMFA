package com.khi.securityservice.security.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class JoinRequestDto {

    private String loginId;
    private String password;
}
