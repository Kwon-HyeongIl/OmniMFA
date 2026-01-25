package com.khi.totpservice.body.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khi.totpservice.body.entity.TotpClientEntity;

import java.util.Optional;

public interface TotpClientRepository extends JpaRepository<TotpClientEntity, Long> {

    Optional<TotpClientEntity> findByProductIdAndProductClientUid(String productId, String productClientUid);
}
