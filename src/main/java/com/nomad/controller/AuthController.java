package com.nomad.controller;

import com.nomad.dto.AuthDto;
import com.nomad.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Auth API (인증)", description = "노마드 앱 로그인 및 VIP 노마드 허브 회원 접속 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "앱 로그인 & 회원 등록", description = "이메일과 이름을 통해 로그인하며 VIP 티어 및 노마드 마일리지를 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
