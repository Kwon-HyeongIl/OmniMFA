package com.khi.securityservice.gateway.repository;

import com.khi.securityservice.gateway.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByLoginId(String loginId);

    UserEntity findByLoginId(String loginId);
}
