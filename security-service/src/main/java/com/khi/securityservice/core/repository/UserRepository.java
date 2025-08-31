package com.khi.securityservice.core.repository;

import com.khi.securityservice.core.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByLoginId(String loginId);

    UserEntity findByLoginId(String loginId);
}
