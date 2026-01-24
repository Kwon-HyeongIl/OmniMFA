package com.khi.totpservice.content.service;

import com.khi.totpservice.content.entity.TotpClientEntity;
import com.khi.totpservice.content.repository.TotpClientRepository;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VerifyServiceTest {

    @Test
    @DisplayName("올바른 코드 검증 통과 여부")
    void verifyCode_success() throws Exception {

        String productId = "550e8400-e29b-41d4-a716-446655440000";
        String productClientUid = "user123";
        String secret = "JBSWY3DPEHPK3PXP";

        TotpClientRepository repo = mock(TotpClientRepository.class);
        TotpClientEntity e = new TotpClientEntity();
        e.setProductId(productId);
        e.setProductClientUid(productClientUid);
        e.setTotpSecretKey(secret);
        when(repo.findByProductIdAndProductClientUid(productId, productClientUid)).thenReturn(Optional.of(e));

        VerifyService service = new VerifyService(repo);

        SystemTimeProvider timeProvider = new SystemTimeProvider();
        int timeIndex = (int) (timeProvider.getTime() / 30);
        String validCode = new DefaultCodeGenerator().generate(secret, timeIndex);

        boolean result = service.verifyCode(productId, productClientUid, validCode);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("잘못된 코드 검증 실패 여부")
    void verifyCode_fail() {

        String productId = "550e8400-e29b-41d4-a716-446655440000";
        String productClientUid = "user123";
        String secret = "JBSWY3DPEHPK3PXP";

        TotpClientRepository repo = mock(TotpClientRepository.class);
        TotpClientEntity e = new TotpClientEntity();
        e.setProductId(productId);
        e.setProductClientUid(productClientUid);
        e.setTotpSecretKey(secret);
        when(repo.findByProductIdAndProductClientUid(productId, productClientUid)).thenReturn(Optional.of(e));

        VerifyService service = new VerifyService(repo);

        boolean result = service.verifyCode(productId, productClientUid, "000000");

        assertThat(result).isFalse();
    }
}
