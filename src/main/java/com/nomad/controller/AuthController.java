package com.nomad.controller;

import com.nomad.dto.AuthDto;
import com.nomad.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Auth API (인증)", description = "Herstory 앱 로그인 및 VIP Herstory 허브 회원 접속 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "신규 회원가입", description = "이메일, 비밀번호, 이름, 연락처를 통해 신규 회원을 등록하고 웰컴 마일리지를 지급합니다.")
    @PostMapping("/register")
    public ResponseEntity<AuthDto.RegisterResponse> register(@RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "앱 로그인", description = "이메일과 비밀번호를 통해 로그인하며 회원 정보, VIP 티어 및 마일리지를 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
