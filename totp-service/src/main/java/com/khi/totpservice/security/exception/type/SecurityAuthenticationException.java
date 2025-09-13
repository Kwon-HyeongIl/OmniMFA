package com.khi.totpservice.security.exception.type;

import org.springframework.security.core.AuthenticationException;

public class SecurityAuthenticationException extends AuthenticationException {

    public SecurityAuthenticationException(String message) {

        super(message);
    }
}

