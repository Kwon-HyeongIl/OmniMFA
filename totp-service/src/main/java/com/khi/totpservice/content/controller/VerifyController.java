package com.khi.totpservice.content.controller;

import com.khi.totpservice.common.api.ApiResponse;
import com.khi.totpservice.content.dto.request.VerifyRequestDto;
import com.khi.totpservice.content.service.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/totp")
public class VerifyController {

    private final VerifyService verifyService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyCode(@RequestBody VerifyRequestDto requestDto) {

        boolean result = verifyService.verifyCode(requestDto.getCustomerServiceClientUid(), requestDto.getCode());

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
