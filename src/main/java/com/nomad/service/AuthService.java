package com.nomad.service;

import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;

    // In-memory verification code storage: phone -> code
    private final Map<String, String> verificationCodeMap = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public AuthDto.RegisterResponse register(AuthDto.RegisterRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일 계정입니다.");
        }

        Member member = memberRepository.save(
                Member.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .name(request.getName() != null ? request.getName() : "신규 회원")
                        .phone(request.getPhone())
                        .vipTier(VipTier.SILVER)
                        .nomadMiles(1000L)
                        .build()
        );

        return AuthDto.RegisterResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .vipTier(member.getVipTier())
                .nomadMiles(member.getNomadMiles())
                .message("Herstory Club 회원가입이 완료되었습니다. (웰컴 1,000 마일리지 적립)")
                .build();
    }

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!member.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return AuthDto.LoginResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .vipTier(member.getVipTier())
                .nomadMiles(member.getNomadMiles())
                .message("Herstory Hub에 성공적으로 접속되었습니다.")
                .build();
    }

    public AuthDto.SendVerificationCodeResponse sendVerificationCode(AuthDto.SendVerificationCodeRequest request) {
        String phone = request.getPhone() != null ? request.getPhone().trim().replaceAll("-", "") : "01012345678";
        
        // Generate 6-digit verification code
        int codeNum = 100000 + random.nextInt(900000);
        String code = String.valueOf(codeNum);
        verificationCodeMap.put(phone, code);

        log.info("[SMS Verification] 휴대폰 번호 {} 로 인증번호 발송 완료: [{}]", phone, code);

        return AuthDto.SendVerificationCodeResponse.builder()
                .phone(request.getPhone())
                .verificationCode(code) // Return code for easy testing/demo
                .expiresInSeconds(180)
                .message(String.format("휴대폰 번호(%s)로 인증번호 6자리가 발송되었습니다. (테스트용 마스터 인증번호: %s 또는 123456)", request.getPhone(), code))
                .build();
    }

    public AuthDto.VerifyCodeResponse verifyCode(AuthDto.VerifyCodeRequest request) {
        String phone = request.getPhone() != null ? request.getPhone().trim().replaceAll("-", "") : "";
        String inputCode = request.getVerificationCode() != null ? request.getVerificationCode().trim() : "";

        // Master bypass code '123456' or matched generated code
        String savedCode = verificationCodeMap.get(phone);
        boolean isVerified = "123456".equals(inputCode) || (savedCode != null && savedCode.equals(inputCode));

        if (isVerified) {
            verificationCodeMap.remove(phone);
            return AuthDto.VerifyCodeResponse.builder()
                    .phone(request.getPhone())
                    .verified(true)
                    .message("휴대폰 인증이 성공적으로 완료되었습니다.")
                    .build();
        }

        return AuthDto.VerifyCodeResponse.builder()
                .phone(request.getPhone())
                .verified(false)
                .message("인증번호가 일치하지 않거나 만료되었습니다.")
                .build();
    }

    @Transactional
    public AuthDto.PasswordResponse resetPassword(AuthDto.ResetPasswordRequest request) {
        Member member = null;
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            member = memberRepository.findByEmail(request.getEmail()).orElse(null);
        }
        if (member == null && request.getPhone() != null && !request.getPhone().isBlank()) {
            member = memberRepository.findByPhone(request.getPhone()).orElse(null);
        }

        if (member == null) {
            throw new IllegalArgumentException("해당 정보로 가입된 회원을 찾을 수 없습니다.");
        }

        String newPassword = (request.getNewPassword() != null && !request.getNewPassword().isBlank()) 
                ? request.getNewPassword() 
                : "1234";

        member.setPassword(newPassword);

        return AuthDto.PasswordResponse.builder()
                .success(true)
                .message(String.format("비밀번호가 성공적으로 재설정되었습니다. (계정: %s)", member.getEmail()))
                .build();
    }
}
