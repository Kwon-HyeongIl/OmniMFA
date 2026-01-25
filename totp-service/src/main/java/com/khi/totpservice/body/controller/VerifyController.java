package com.khi.totpservice.body.controller;

import com.khi.totpservice.api.api.ApiResponse;
import com.khi.totpservice.body.dto.request.VerifyRequestDto;
import com.khi.totpservice.body.service.VerifyService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/totp")
public class VerifyController {

    private final VerifyService verifyService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyCode(@RequestHeader("Product-Id") String productId,
            @RequestBody VerifyRequestDto requestDto) {

        boolean result = verifyService.verifyCode(productId, requestDto.getProductClientUid(), requestDto.getCode());

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
