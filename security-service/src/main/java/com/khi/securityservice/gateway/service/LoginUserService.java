package com.khi.securityservice.gateway.service;

import com.khi.securityservice.gateway.dto.form.UserPrincipal;
import com.khi.securityservice.gateway.entity.SecurityUserPrincipalEntity;
import com.khi.securityservice.gateway.entity.UserEntity;
import com.khi.securityservice.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String loginId = username;
        UserEntity user = userRepository.findByLoginId(loginId);

        if (user != null) {

            log.info("DB에 유저 존재 loginId: {}", loginId);

            SecurityUserPrincipalEntity userPrincipalEntity = new SecurityUserPrincipalEntity();

            userPrincipalEntity.setLoginId(loginId);
            userPrincipalEntity.setPassword(user.getPassword());
            userPrincipalEntity.setRole("ROLE_USER");

            return new UserPrincipal(userPrincipalEntity);
        }

        log.warn("존재하지 않는 유저: {}", loginId);

        return null;
    }
}