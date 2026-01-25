package com.khi.securityservice.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khi.securityservice.security.entity.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByLoginId(String loginId);

    UserEntity findByLoginId(String loginId);
}
