package com.khi.totpservice.body.service;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.khi.totpservice.body.entity.TotpClientEntity;
import com.khi.totpservice.body.repository.TotpClientRepository;
import com.khi.totpservice.client.OnboardingFeignClient;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final TotpClientRepository totpClientRepository;
    private final OnboardingFeignClient onboardingFeignClient;

    public String generateQrCode(String productId, String productClientUid) {

        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String totpSecretKey = secretGenerator.generate();

        TotpClientEntity client = new TotpClientEntity();
        client.setProductId(productId);
        client.setProductClientUid(productClientUid);
        client.setTotpSecretKey(totpSecretKey);
        client.setEnabled(true);
        totpClientRepository.save(client);

        String productName = onboardingFeignClient.getProductNameByProductId(productId)
                .orElse(productId);

        QrData data = new QrData.Builder()
                .label(productName + ":" + productClientUid)
                .secret(totpSecretKey) // totpSecretKey로 qr을 구분
                .issuer("OmniMFA")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {

            QrGenerator generator = new ZxingPngQrGenerator();
            byte[] imageData = generator.generate(data);

            return getDataUriForImage(imageData, generator.getImageMimeType());

        } catch (QrGenerationException e) {

            throw new RuntimeException("QR 코드 생성에 실패하였습니다.", e);
        }
    }
}
