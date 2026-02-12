package com.khi.totpservice.body.service;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.devh.boot.grpc.client.inject.GrpcClient;

import com.khi.totpservice.body.entity.TotpClientEntity;
import com.khi.totpservice.body.repository.TotpClientRepository;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetupService {

    private final TotpClientRepository totpClientRepository;

    @GrpcClient("onboarding-service")
    private com.khi.product.grpc.ProductGrpcServiceGrpc.ProductGrpcServiceBlockingStub productGrpcServiceBlockingStub;

    @Transactional
    public String generateQrCode(String productId, String productClientUid) {

        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String totpSecretKey = secretGenerator.generate();

        // gRPC를 사용하여 제품 이름 조회
        log.info("gRPC 요청 송신 시작");
        com.khi.product.grpc.ProductRequest grpcRequest = com.khi.product.grpc.ProductRequest.newBuilder()
                .setProductId(productId)
                .build();

        String productName = productGrpcServiceBlockingStub.getProductName(grpcRequest).getProductName();
        log.info("gRPC 요청 수신 완료");

        TotpClientEntity client = new TotpClientEntity();
        client.setProductId(productId);
        client.setProductClientUid(productClientUid);
        client.setTotpSecretKey(totpSecretKey);
        client.setEnabled(true);
        totpClientRepository.save(client);

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
