package com.khi.totpservice.content.controller;

import com.khi.totpservice.common.api.ApiResponse;
import com.khi.totpservice.content.dto.request.VerifyRequestDto;
import com.khi.totpservice.content.service.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/totp")
public class VerifyController {

    private final VerifyService verifyService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyCode(@RequestHeader("product-id") String productId,
            @RequestBody VerifyRequestDto requestDto) {

        boolean result = verifyService.verifyCode(productId, requestDto.getProductClientUid(), requestDto.getCode());

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
