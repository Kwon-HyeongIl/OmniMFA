package com.khi.securityservice.core.service;

import com.khi.securityservice.core.principal.SecurityUserPrincipal;
import com.khi.securityservice.core.entity.security.SecurityUserPrincipalEntity;
import com.khi.securityservice.core.entity.domain.UserEntity;
import com.khi.securityservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 사용자가 입력한 비밀번호와, 여기서 DB에서 가져온 비밀번호를 스프링 시큐리티가 자체적으로 검증
 - SecurityConfig에 등록한 PasswordEncoder를 스프링 시큐리티가 자체적으로 사용해서 비교
 - loginId는 내가 직접 검증(DB에 loginId가 존재하는지 확인)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String loginId = username; // 사용자가 입력한 loginId
        UserEntity user = userRepository.findByLoginId(loginId);

        if (user != null) {

            log.info("DB에 유저 존재 uid: {}", user.getId());

            SecurityUserPrincipalEntity userPrincipalEntity = new SecurityUserPrincipalEntity();

            userPrincipalEntity.setUid(user.getId());
            userPrincipalEntity.setPassword(user.getPassword());
            userPrincipalEntity.setRole("ROLE_USER");

            return new SecurityUserPrincipal(userPrincipalEntity);
        }

        log.warn("존재하지 않는 유저: {}", loginId);

        return null;
    }
}