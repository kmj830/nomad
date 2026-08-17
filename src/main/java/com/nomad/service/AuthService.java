package com.nomad.service;

import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;

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
}
