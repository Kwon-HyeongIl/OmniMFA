package com.khi.onboardingservice.security.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserPrincipalEntity {

    private String uid;
    private String password;
    private String role;
}
