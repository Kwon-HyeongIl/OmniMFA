package com.khi.securityservice.gateway.controller;

import com.khi.securityservice.common.api.ApiResponse;
import com.khi.securityservice.gateway.dto.JoinRequestDto;
import com.khi.securityservice.gateway.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "사용자 인증 및 권한 관련 API")
@RestController
@RequestMapping("/security")
public class UserController {

    /* 실제 사용되지 않지만 Swagger에 표시하기 위해 등록 */
    @Operation(
            summary = "회원가입",
            description = "-"
    )
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<?>> join(@RequestBody JoinRequestDto dto) {

        return ResponseEntity.ok(ApiResponse.success());
    }

    /* 실제 사용되지 않지만 Swagger에 표시하기 위해 등록 */
    @Operation(
            summary = "로그인",
            description = "Access 토큰은 헤더에, Refresh 토큰은 쿠키에 담겨서 반환"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(LoginRequestDto dto) {

        return ResponseEntity.ok(ApiResponse.success());
    }

    /* 실제 사용되지 않지만 Swagger에 표시하기 위해 등록 */
    @Operation(
            summary = "로그아웃",
            description = "-"
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logut() {

        return ResponseEntity.ok(ApiResponse.success());
    }
}
