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

    @Operation(summary = "회원가입 휴대폰 SMS 인증번호 발송", description = "입력한 휴대폰 번호로 6자리 SMS 인증번호를 발송합니다. (테스트용 마스터 코드 123456 지원)")
    @PostMapping({"/phone/send-code", "/phone/send"})
    public ResponseEntity<AuthDto.SendVerificationCodeResponse> sendVerificationCode(@RequestBody AuthDto.SendVerificationCodeRequest request) {
        return ResponseEntity.ok(authService.sendVerificationCode(request));
    }

    @Operation(summary = "회원가입 휴대폰 SMS 인증번호 검증", description = "전송된 6자리 인증번호의 유효성을 검증합니다.")
    @PostMapping({"/phone/verify-code", "/phone/verify"})
    public ResponseEntity<AuthDto.VerifyCodeResponse> verifyCode(@RequestBody AuthDto.VerifyCodeRequest request) {
        return ResponseEntity.ok(authService.verifyCode(request));
    }

    @Operation(summary = "비밀번호 찾기 및 재설정", description = "가입된 이메일 또는 휴대폰 번호를 확인하여 새로운 비밀번호로 재설정합니다.")
    @PostMapping({"/password/reset", "/password/find"})
    public ResponseEntity<AuthDto.PasswordResponse> resetPassword(@RequestBody AuthDto.ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
