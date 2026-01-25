package com.khi.securityservice.security.entity.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SecurityUserPrincipalEntity {

    private Long uid;
    private String password;
    private String role;
}