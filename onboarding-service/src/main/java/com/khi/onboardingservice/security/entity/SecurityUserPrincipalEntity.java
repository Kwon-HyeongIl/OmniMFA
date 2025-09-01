package com.khi.onboardingservice.security.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserPrincipalEntity {

    private String loginId;
    private String password;
    private String role;
}
