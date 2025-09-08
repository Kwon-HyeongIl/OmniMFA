package com.khi.totpservice.content.service;

import com.khi.totpservice.content.entity.TotpClientEntity;
import com.khi.totpservice.content.repository.TotpClientRepository;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final TotpClientRepository totpClientRepository;

    public String generateQrCode(String clientId, String clientUid) {

        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String totpSecretKey = secretGenerator.generate();

        TotpClientEntity client =  new TotpClientEntity();
        client.setClientId(clientId);
        client.setClientUId(clientUid);
        client.setTotpSecretKey(totpSecretKey);
        client.setEnabled(true);
        totpClientRepository.save(client);

        QrData data = new QrData.Builder()
                .label(clientUid)
                .secret(totpSecretKey)
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
