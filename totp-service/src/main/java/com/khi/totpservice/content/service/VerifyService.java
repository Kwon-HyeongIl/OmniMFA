package com.khi.totpservice.content.service;

import com.khi.totpservice.content.entity.TotpClientEntity;
import com.khi.totpservice.content.repository.TotpClientRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyService {

    private final TotpClientRepository totpClientRepository;

    public boolean verifyCode(String productId, String productClientUid, String code) {

        TotpClientEntity totpClient = totpClientRepository
                .findByProductIdAndProductClientUid(productId, productClientUid)
                .orElseThrow(() -> new RuntimeException("TOTP 클라이언트가 존재하지 않습니다."));

        String totpSecretKey = totpClient.getTotpSecretKey();

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        boolean result = verifier.isValidCode(totpSecretKey, code);

        return result;
    }
}
