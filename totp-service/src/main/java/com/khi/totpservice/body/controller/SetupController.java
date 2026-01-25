package com.khi.totpservice.body.controller;

import com.khi.totpservice.api.api.ApiResponse;
import com.khi.totpservice.body.dto.request.SetupRequestDto;
import com.khi.totpservice.body.dto.response.SetupResponseDto;
import com.khi.totpservice.body.service.SetupService;

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
