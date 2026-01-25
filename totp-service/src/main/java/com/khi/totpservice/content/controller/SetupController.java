package com.khi.totpservice.content.controller;

import com.khi.totpservice.common.api.ApiResponse;
import com.khi.totpservice.content.dto.request.SetupRequestDto;
import com.khi.totpservice.content.dto.response.SetupResponseDto;
import com.khi.totpservice.content.service.SetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/totp")
public class SetupController {

    private final SetupService setupService;

    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<?>> setupTotp(@RequestHeader("Product-Id") String productId,
            @RequestBody SetupRequestDto requestDto) {

        String qrCodeDataUri = setupService.generateQrCode(productId, requestDto.getProductClientUid());

        SetupResponseDto responseDto = SetupResponseDto.builder()
                .qrCodeDataUri(qrCodeDataUri)
                .build();

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}
