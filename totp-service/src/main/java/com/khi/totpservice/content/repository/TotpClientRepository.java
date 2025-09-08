package com.khi.totpservice.content.repository;

import com.khi.totpservice.content.entity.TotpClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TotpClientRepository extends JpaRepository<TotpClientEntity, Long> {
}
