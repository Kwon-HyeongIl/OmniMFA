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

        String clientUid = "0";
        String secret = "JBSWY3DPEHPK3PXP";

        TotpClientRepository repo = mock(TotpClientRepository.class);
        TotpClientEntity e = new TotpClientEntity();
        e.setCustomerServiceClientUid(clientUid);
        e.setTotpSecretKey(secret);
        when(repo.findByCustomerServiceClientUid(clientUid)).thenReturn(Optional.of(e));

        VerifyService service = new VerifyService(repo);

        SystemTimeProvider timeProvider = new SystemTimeProvider();
        int timeIndex = (int) (timeProvider.getTime() / 30);
        String validCode = new DefaultCodeGenerator().generate(secret, timeIndex);

        boolean result = service.verifyCode(clientUid, validCode);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("잘못된 코드 검증 실패 여부")
    void verifyCode_fail() {

        String clientUid = "0";
        String secret = "JBSWY3DPEHPK3PXP";

        TotpClientRepository repo = mock(TotpClientRepository.class);
        TotpClientEntity e = new TotpClientEntity();
        e.setCustomerServiceClientUid(clientUid);
        e.setTotpSecretKey(secret);
        when(repo.findByCustomerServiceClientUid(clientUid)).thenReturn(Optional.of(e));

        VerifyService service = new VerifyService(repo);

        boolean result = service.verifyCode(clientUid, "000000");

        assertThat(result).isFalse();
    }
}
