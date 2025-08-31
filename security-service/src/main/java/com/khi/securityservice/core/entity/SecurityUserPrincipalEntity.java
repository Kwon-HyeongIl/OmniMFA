package com.khi.securityservice.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserPrincipalEntity {

    private String loginId;
    private String role;
}